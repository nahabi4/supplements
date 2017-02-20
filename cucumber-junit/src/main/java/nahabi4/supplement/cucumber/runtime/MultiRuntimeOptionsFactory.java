package nahabi4.supplement.cucumber.runtime;

import cucumber.api.CucumberOptions;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import nahabi4.supplement.cucumber.api.CucumberGroupsOptions;
import nahabi4.supplement.cucumber.api.junit.CucumberJar;

import java.lang.annotation.IncompleteAnnotationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Uses to retrieve information about multiple Runtimes that used in {@link CucumberJar} and set in {@link CucumberGroupsOptions}
 * <p>
 * <p>
 * Unfortunately cannot extend this class directly from {@link RuntimeOptionsFactory} as all methods are private, thus cannot use it
 * and have to copy/paste almost all.
 * </p>
 */
public class MultiRuntimeOptionsFactory {
    private final Class clazz;
    private boolean featuresSpecified = false;
    private boolean glueSpecified = false;
    private boolean pluginSpecified = false;

    public MultiRuntimeOptionsFactory(Class clazz) {
        this.clazz = clazz;
    }

    public List<RuntimeOptions> create() {
        List<RuntimeOptions> runtimeOptionsList = new ArrayList<>();

        final List<String> defaultArgs = buildCommonArgsFromOptions(getOptions(clazz));

        CucumberGroupsOptions groupsOptions = getGroupsOptions(clazz);
        for (CucumberOptions groupOptions : groupsOptions.value()) {
            List<String> args = buildSpecificArgsFromOptions(groupOptions);
            args.addAll(defaultArgs);
            runtimeOptionsList.add(new RuntimeOptions(args));

        }
        return runtimeOptionsList;
    }

    private List<String> buildCommonArgsFromOptions(CucumberOptions options) {
        List<String> args = new ArrayList<>();

        if (options != null) {
            addDryRun(options, args);
            addMonochrome(options, args);
            addSnippets(options, args);
            addStrict(options, args);

            addTags(options, args);
            addGlue(options, args);
        }
        return args;
    }

    private List<String> buildSpecificArgsFromOptions(CucumberOptions options) {
        List<String> args = new ArrayList<>();

        if (options != null) {
            addTags(options, args);
            addName(options, args);
            addGlue(options, args);
            addFeatures(options, args);
        }

        validateOptions();

        return args;
    }

    private void validateOptions() {
        String validationErrorMessage = "";
        if (!glueSpecified) {
            validationErrorMessage += "Glue should be specified. ";
        }
        if (!featuresSpecified) {
            validationErrorMessage += "Features should be specified. ";
        }

        if (!validationErrorMessage.isEmpty()) {
            throw new IncompleteAnnotationException(CucumberGroupsOptions.class, validationErrorMessage);
        }
    }

    private void addName(CucumberOptions options, List<String> args) {
        for (String name : options.name()) {
            args.add("--name");
            args.add(name);
        }
    }

    private void addSnippets(CucumberOptions options, List<String> args) {
        args.add("--snippets");
        args.add(options.snippets().toString());
    }

    private void addDryRun(CucumberOptions options, List<String> args) {
        if (options.dryRun()) {
            args.add("--dry-run");
        }
    }

    private void addMonochrome(CucumberOptions options, List<String> args) {
        if (options.monochrome() || runningInEnvironmentWithoutAnsiSupport()) {
            args.add("--monochrome");
        }
    }

    private void addTags(CucumberOptions options, List<String> args) {
        for (String tags : options.tags()) {
            args.add("--tags");
            args.add(tags);
        }
    }

    private void addStrict(CucumberOptions options, List<String> args) {
        if (options.strict()) {
            args.add("--strict");
        }
    }

    private void addFeatures(CucumberOptions options, List<String> args) {
        if (options != null && options.features().length != 0) {
            Collections.addAll(args, options.features());
            featuresSpecified = true;
        }
    }

    private void addGlue(CucumberOptions options, List<String> args) {
        for (String glue : options.glue()) {
            args.add("--glue");
            args.add(glue);
            glueSpecified = true;
        }
    }

    private boolean runningInEnvironmentWithoutAnsiSupport() {
        boolean intelliJidea = System.getProperty("idea.launcher.bin.path") != null;
        // TODO: What does Eclipse use?
        return intelliJidea;
    }

    private CucumberOptions getOptions(Class<?> clazz) {
        return clazz.getAnnotation(CucumberOptions.class);
    }

    private CucumberGroupsOptions getGroupsOptions(Class<?> clazz) {
        return clazz.getAnnotation(CucumberGroupsOptions.class);
    }
}
