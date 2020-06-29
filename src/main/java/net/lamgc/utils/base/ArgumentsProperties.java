package net.lamgc.utils.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentsProperties extends HashMap<String, String>{

    private static final long serialVersionUID = 3008463812837214134L;

    private static transient final String[] defaultKeyFlags = new String[] {
        "--",
        "-"
    };

    private final static Pattern pattern = Pattern.compile("/\\s*(\".+?\"|[^:\\s])+((\\s*:\\s*(\".+?\"|[^\\s])+)|)|(\".+?\"|[^\"\\s])+");

    private String[] keyFlags;

    private String[] rawArguments;

    private ArrayList<String> keyList = new ArrayList<>();

    /**
     * 构造一个ArgumentsProperties.
     * 该ArgumentsProperties将使用默认的参数标识来解析参数.
     */
    public ArgumentsProperties(){
        this(null);
    }

    /**
     * 构造一个ArgumentsProperties并解析参数数组中的参数.
     * @param args 参数列表, 比如从{@code main(String[] args)}方法参数中获得的参数.
     */
    public ArgumentsProperties(String[] args){
        this(args, null);
    }

    /**
     * 构造一个ArgumentsProperties
     * @param args 待解析的参数列表
     * @param keyFlags 参数Key标识列表
     */
    public ArgumentsProperties(String[] args, String[] keyFlags){
        this.keyFlags = keyFlags == null ? defaultKeyFlags : keyFlags;
        if(args != null){
            this.load(args);
        }
    }

    /**
     * 将参数加载进行处理
     * @param argument 参数文本
     */
    public void load(String argument) {
        Matcher matcher = pattern.matcher(argument);
        ArrayList<String> argsList = new ArrayList<>();
        while (matcher.find()) {
            argsList.add(matcher.group());
        }
        load(argsList.toArray(new String[0]));
    }

    /**
     * 将参数数组加载进行处理.<br>
     * 注意: 此前加载的内容将会被清空!
     * @param args 参数数组
     */
    public void load(final String[] args){
        rawArguments = args;
        keyList.clear();
        super.clear();

        String lastKey = null;
        for(String arg : args){
            String cacheKey = getKeyFilterFlag(arg);
            if(cacheKey != null){
                if(lastKey != null){
                    super.put(lastKey, "");
                    keyList.add(lastKey);
                }

                //尝试取值
                String cacheValue = getValueFromKey(arg);
                if(cacheValue == null){
                    //等待取值
                    lastKey = cacheKey;
                }else{
                    super.put(cacheKey, cacheValue);
                    keyList.add(cacheKey);
                }
            }else{
                //作为参数值存储
                super.put(lastKey, arg);
                keyList.add(lastKey);
                lastKey = null;
            }
        }
        if(lastKey != null){
            super.put(lastKey, "");
            keyList.add(lastKey);
        }
    }
    
    /**
     * 键参数上是否有值
     * @return 有参数值则返回true
     */
    private String getValueFromKey(String s){
        int index;
        if((index = s.indexOf(":")) == -1 && (index = s.indexOf("=")) == -1){
            return null;
        }
        return s.substring(index + 1).trim();
    }
    
    /**
     * 过滤key标识符, 如果该字符串没有以预定的key标识符开头, 则不认定为是一个key, 返回null
     * @param s 需要检查并过滤的字符串
     * @return 如果是参数名, 返回已过滤标识符的参数名, 如果不是, 返回null
     */
    private String getKeyFilterFlag(String s){
        int flagIndex = -1;
        for(int i = 0; i < keyFlags.length; i++){
            if(s.indexOf(keyFlags[i]) == 0){
                flagIndex = i;
                break;
            }
        }
        if(flagIndex == -1){
            return null;
        }

        int valueFlag = s.indexOf(":");
        if(valueFlag == -1){
            if((valueFlag = s.indexOf("=")) == -1) {
                return s.replace(keyFlags[flagIndex], "").trim();
            }
        }
        return s.substring(keyFlags[flagIndex].length(), valueFlag).trim();
    }


    /**
     * 获取指定参数的值.<br>
     * 当参数存在, 但没有赋值时, 方法将返回空字符串而非null.<br>
     * @param key 要获取值的参数名
     * @return 如果输入的参数中有指定的参数, 则返回非null(即使没有值), 如果该参数不存在, 返回null
     */
    public String getValue(String key){
        if(!super.containsKey(key)){
            return null;
        }
        return super.get(key);
    }

    /**
     * 获取原始输入的参数key列表.
     * 该列表中的参数属性带有flag, 无法从ArgumentsProperties中获取,
     * 要获取能够从ArgumentsProperties中取值的key列表请使用{@link #getKeys()}
     * @return 返回存储参数列表的List
     */
    public List<String> getKeysWithFlag(){
        return keyList;
    }

    /**
     * 获取经过解析的key列表.
     * @return 返回成功解析的参数key列表
     */
    public Set<String> getKeys(){
        return super.keySet();
    }

    /**
     * 获取最后一次调用{@link #load(String[])}时提供的原始参数数组
     * @return 最后一次加载的原始参数数组
     */
    public String[] getRawArguments() {
        return rawArguments;
    }
}