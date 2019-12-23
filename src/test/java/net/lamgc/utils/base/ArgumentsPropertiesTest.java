package net.lamgc.utils.base;

import net.lamgc.utils.base.ArgumentsProperties;
import org.junit.Test;

import java.util.Arrays;

public class ArgumentsPropertiesTest{

    private final String[] argsList = {"/a", "233", "/b : 12", "-c", "34", "-d:56", "--EF", "78", "--GH:90", "/IJ", "--KL", "-MN"};

    @Test
    public void loadTest(){
        //解析参数
        ArgumentsProperties argsProperties = new ArgumentsProperties(argsList);
        System.out.println(Arrays.toString(argsProperties.getKeysWithFlag().toArray(new String[0])));
        for(String key : argsProperties.keySet().toArray(new String[0])){
            System.out.println(key + ": " + argsProperties.get(key));
        }
    }

}
