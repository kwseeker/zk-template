package top.kwseeker.zk.discovery.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    public static Properties load(File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            return props;
        } finally {
            if(in != null) {
                in.close();
            }
        }
    }

    public static Properties load(String path) throws IOException{
        InputStream in = null;
        try {
            in = PropertyUtil.class.getClassLoader().getResourceAsStream(path);
            Properties props = new Properties();
            props.load(in);
            return props;
        }finally{
            if(in != null) {
                in.close();
            }
        }
    }
}
