package com.clarity.ipmsbackend.utils;

/**
 * 编码自动生成器
 *
 * @author: clarity
 * @date: 2023年02月23日 11:44
 */

public class CodeAutoGenerator {

    public static String literallyCode(String codeStr) {
        StringBuilder englishCode = new StringBuilder();
        StringBuilder arabCode = new StringBuilder();
        StringBuilder tempCode = new StringBuilder();
        char[] codeChars = codeStr.toCharArray();
        for (int i = 0; i < codeChars.length; i++) {
            if (Character.isUpperCase(codeChars[i])) {
                englishCode.append(codeChars[i]);
            } else {
                if (codeChars[i] == '0') {
                    arabCode.append(codeChars[i]);
                } else {
                    tempCode.append(codeChars[i]);
                    if (i == codeChars.length - 1) {
                        int lastCodeNum = Integer.parseInt(tempCode.toString());
                        lastCodeNum++;
                        tempCode = new StringBuilder(String.valueOf(lastCodeNum));
                        arabCode.append(tempCode);
                    }
                }
            }
        }
        System.out.println(tempCode);
        return englishCode + arabCode.toString();
    }
}
