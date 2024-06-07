package net.risesoft.converter;

import javax.persistence.AttributeConverter;

import net.risesoft.util.Y9Encrytor;
import net.risesoft.y9.Y9Context;

public class EncryptConverter implements AttributeConverter<String,String> {
	
	@Override
    public String convertToDatabaseColumn(String attribute) {
		boolean convertEnabled = Boolean.valueOf(Y9Context.getProperty("y9.common.convertEnabled"));
		//加密
		if(convertEnabled) {
			try {
				Y9Encrytor y9Encrytor = new Y9Encrytor();
				attribute = y9Encrytor.Encrytor(attribute);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String data) {
    	boolean convertEnabled = Boolean.valueOf(Y9Context.getProperty("y9.common.convertEnabled"));
    	//解密
    	if(convertEnabled) {
    		try {
    			Y9Encrytor y9Encrytor = new Y9Encrytor();
    			data = y9Encrytor.Decryptor(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
        return data;
    }

}
