package net.lamgc.utils.base;

import net.lamgc.utils.base.ArgumentsProperties;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;

public class ArgumentsPropertiesTest{

    private final String[] argsList = {"/a", "233", "/b : 12", "-c", "34", "-d:56", "--EF", "78", "--GH:90", "/IJ", "--KL", "-MN"};

    private final static HashMap<String, String> valueList = new HashMap<>();

    static {
        valueList.put("a", "233");
        valueList.put("b", "12");
        valueList.put("c", "34");
        valueList.put("d", "56");
        valueList.put("EF", "78");
        valueList.put("GH", "90");
        valueList.put("IJ", "");
        valueList.put("KL", "");
        valueList.put("MN", "");
    }


    @Test
    public void loadTest(){
        Logger log = LoggerFactory.getLogger(ArgumentsPropertiesTest.class.getSimpleName());
        //解析参数
        ArgumentsProperties argsProperties = new ArgumentsProperties(argsList);
        System.out.println(Arrays.toString(argsProperties.getKeysWithFlag().toArray(new String[0])));
        int count = 0;
        for(String key : argsProperties.keySet().toArray(new String[0])){
            String value =  argsProperties.get(key);
            log.info("{}: {}", key, value);
            Assert.assertEquals(value, valueList.get(key));

        }
    }

}

