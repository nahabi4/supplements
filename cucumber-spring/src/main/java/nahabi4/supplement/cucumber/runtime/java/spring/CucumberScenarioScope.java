package nahabi4.supplement.cucumber.runtime.java.spring;

import org.springframework.context.annotation.Scope;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(CucumberScope.SCENARIO)
public @interface CucumberScenarioScope {
}
