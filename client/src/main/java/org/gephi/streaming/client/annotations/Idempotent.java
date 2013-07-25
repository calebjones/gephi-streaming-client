package org.gephi.streaming.client.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating the method is idempotent or should be 
 * implemented as such.
 * 
 * @author Caleb Jones &lt;<a href="mailto:calebjones@gmail.com">calebjones@gmail.com</a>&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Inherited
@Documented
public @interface Idempotent {

}
