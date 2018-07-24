package hh.szu.utils;

import java.util.UUID;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/7/24
 * Time:11:06
 */
public class CommonUtils {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }
}
