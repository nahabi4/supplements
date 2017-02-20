package nahabi4.supplement.cucumber.api;

import cucumber.api.CucumberOptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Additional cucumber configuration allowed to split Features for different Runtimes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CucumberGroupsOptions {

    CucumberOptions[] value();

}
