package net.lamgc.utils.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArgumentsProperties extends HashMap<String, String>{

	private static final long serialVersionUID = 3008463812837214134L;
	
	private static transient final String[] defaultKeyFlags = new String[] {
		"--",
		"-",
		"/"
	};

	private transient String[] keyFlags;

	private ArrayList<String> keyList = new ArrayList<>();

    public ArgumentsProperties(){
		this(null, null);
	}

	public ArgumentsProperties(String[] args){
		this(args, null);
	}

	// 对指定参数提供过滤选项，以在使用不正确选项的时候抛出异常
    public ArgumentsProperties(String[] args, String[] keyFlags){
		this.keyFlags = keyFlags == null ? defaultKeyFlags : keyFlags;
		if(args != null){
			this.load(args);
		}
    }

    /*
    后期提供的功能：
    解析事件
    对选项的限制


     */

    /*
		//通用格式
        -key value
        -key:value
		
		//长参数格式
        --key value
        --key:value
		
		//windows格式
		/key value
        /key:value
        多个值需要使用:(windows) ;(linux) (自动识别)(以第一个为准)
    */

	/**
	 * 将参数数组加载进行处理
	 * @param args 参数数组
	 */
    public void load(String[] args){
		String lastKey = null;
		for(String arg : args){
			String cacheKey = getKey(arg);
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
		if((index = s.indexOf(":")) == -1){
			return null;
		}
		return s.substring(index + 1).trim();
	}
	
	/**
	 * 过滤key标识符, 如果该字符串没有以预定的key标识符开头, 则不认定为是一个key, 返回null
	 * @param s 需要检查并过滤的字符串
	 * @return 如果是参数名, 返回已过滤标识符的参数名, 如果不是, 返回null
	 */
	private String getKey(String s){
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
            return s.replace(keyFlags[flagIndex], "").trim();
        }else{
		    return s.substring(keyFlags[flagIndex].length(), valueFlag).trim();
        }

	}


	/**
	 * 获取指定参数的值
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
     * 获取有序的参数列表,
     *
     * @return 返回存储参数列表的List
     */
	public List<String> getKeyList(){
	    return keyList;
    }

}