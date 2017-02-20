package nahabi4.supplement.cucumber.api.junit;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitOptions;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;
import nahabi4.supplement.cucumber.api.CucumberGroupsOptions;
import nahabi4.supplement.cucumber.runtime.MultiRuntimeOptionsFactory;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Classes annotated with {@code @RunWith(CucumberJar.class)} will run a Cucumber Features that separated by groups.
 * This allows to specify different Glue (java classes and spring configurations) for particular sets of Features in the same time using sole JUnitReporter.
 * The class should be empty without any fields or methods.
 * </p>
 * <p>
 * Have to be annotated with {@link CucumberOptions} and {@link CucumberGroupsOptions}.
 * </p>
 * <p>
 * Annotation {@link CucumberOptions} uses to define common settings - {@code dryRun, strict, format, plugin, monochrome, snippets}.
 * Also {@code glue, tags} specified in this annotation will be added to each group glue and tags respectively.
 * </p>
 * <p>
 * Annotation {@link CucumberGroupsOptions} consists of nested collection of {@link CucumberOptions} annotations,
 * where each {@link CucumberOptions} annotation represents separate group.
 * From this annotation only four properties will be taken into account - {@code features, glue, tags, name}
 * </p>
 * <p>
 * Unfortunately cannot extend this class directly from {@link Cucumber} as whole initialization process happens in constructor,
 * thus don't want to create object properties instances twice and have to copy/paste.
 * </p>
 *
 * @see CucumberOptions
 */
public class CucumberJar extends ParentRunner<FeatureRunner> {
    private final JUnitReporter jUnitReporter;
    private final List<FeatureRunner> children = new ArrayList<>();
    private final List<Runtime> runtimes = new ArrayList<>();

    /**
     * Constructor called by JUnit.
     *
     * @param clazz the class with the @RunWith annotation.
     * @throws java.io.IOException                         if there is a problem
     * @throws org.junit.runners.model.InitializationError if there is another problem
     */
    public CucumberJar(Class clazz) throws InitializationError, IOException {
        super(clazz);
        ClassLoader classLoader = clazz.getClassLoader();
        Assertions.assertNoCucumberAnnotatedMethods(clazz);

        RuntimeOptionsFactory commonRuntimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        RuntimeOptions commonRuntimeOptions = commonRuntimeOptionsFactory.create();

        MultiRuntimeOptionsFactory groupsRuntimeOptionsFactory = new MultiRuntimeOptionsFactory(clazz);
        List<RuntimeOptions> groupsRuntimeOptions = groupsRuntimeOptionsFactory.create();

        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        final JUnitOptions junitOptions = new JUnitOptions(commonRuntimeOptions.getJunitOptions());
        jUnitReporter = new JUnitReporter(commonRuntimeOptions.reporter(classLoader), commonRuntimeOptions.formatter(classLoader), commonRuntimeOptions.isStrict(), junitOptions);

        for (RuntimeOptions currentGroupRuntimeOptions : groupsRuntimeOptions) {
            final Runtime currentGroupRuntime = createRuntime(resourceLoader, classLoader, currentGroupRuntimeOptions);
            final List<CucumberFeature> currentGroupCucumberFeatures = currentGroupRuntimeOptions.cucumberFeatures(resourceLoader);
            addChildren(currentGroupCucumberFeatures, currentGroupRuntime);
        }
    }

    /**
     * Create the Runtime. Can be overridden to customize the runtime or backend.
     *
     * @param resourceLoader used to load resources
     * @param classLoader    used to load classes
     * @param runtimeOptions configuration
     * @return a new runtime
     * @throws InitializationError if a JUnit error occurred
     * @throws IOException         if a class or resource could not be loaded
     */
    protected Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader,
                                    RuntimeOptions runtimeOptions) throws InitializationError, IOException {
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    @Override
    public List<FeatureRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        jUnitReporter.done();
        jUnitReporter.close();
        for (Runtime runtime : runtimes) {
            runtime.printSummary();
        }
    }

    private void addChildren(List<CucumberFeature> cucumberFeatures, Runtime runtime) throws InitializationError {
        for (CucumberFeature cucumberFeature : cucumberFeatures) {
            children.add(new FeatureRunner(cucumberFeature, runtime, jUnitReporter));
        }
    }
}