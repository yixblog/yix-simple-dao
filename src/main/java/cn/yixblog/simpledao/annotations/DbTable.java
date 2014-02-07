package cn.yixblog.simpledao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * config table
 * Created by dyb on 14-1-31.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DbTable {
    public String value();
    public String primaryKey() default "id";
    public boolean primaryAuto() default true;
}
