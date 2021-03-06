/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.internal.util;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * The Class JavaBeansUtil.
 *
 * @author Jeff Butler
 */
public class JavaBeansUtil {

    /**
     * Instantiates a new java beans util.
     */
    private JavaBeansUtil() {
        super();
    }

    /**
     * JavaBeans rules:
     * 
     * eMail &gt; geteMail() firstName &gt; getFirstName() URL $gt; getURL() XAxis &gt; getXAxis() a &gt; getA() B &gt;
     * invalid - this method assumes that this is not the case. Call getValidPropertyName first. Yaxis &gt; invalid -
     * this method assumes that this is not the case. Call getValidPropertyName first.
     *
     * @param property
     *            the property
     * @param fullyQualifiedJavaType
     *            the fully qualified java type
     * @return the getter method name
     */
    public static String getGetterMethodName(String property,
            FullyQualifiedJavaType fullyQualifiedJavaType) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        if (fullyQualifiedJavaType.equals(FullyQualifiedJavaType
                .getBooleanPrimitiveInstance())) {
            sb.insert(0, "is"); //$NON-NLS-1$
        } else {
            sb.insert(0, "get"); //$NON-NLS-1$
        }

        return sb.toString();
    }

    /**
     * JavaBeans rules:
     * 
     * eMail &gt; seteMail() firstName &gt; setFirstName() URL &gt; setURL() XAxis &gt; setXAxis() a &gt; setA() B &gt;
     * invalid - this method assumes that this is not the case. Call getValidPropertyName first. Yaxis &gt; invalid -
     * this method assumes that this is not the case. Call getValidPropertyName first.
     *
     * @param property
     *            the property
     * @return the setter method name
     */
    public static String getSetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        sb.insert(0, "set"); //$NON-NLS-1$

        return sb.toString();
    }

    /**
     * Gets the camel case string.
     *
     * @param inputString
     *            the input string
     * @param firstCharacterUppercase
     *            the first character uppercase
     * @return the camel case string
     */
    public static String getCamelCaseString(String inputString,
            boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
            case '_':
            case '-':
            case '@':
            case '$':
            case '#':
            case ' ':
            case '/':
            case '&':
                if (sb.length() > 0) {
                    nextUpperCase = true;
                }
                break;

            default:
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * This method ensures that the specified input string is a valid Java property name. The rules are as follows:
     * 
     * 1. If the first character is lower case, then OK 2. If the first two characters are upper case, then OK 3. If the
     * first character is upper case, and the second character is lower case, then the first character should be made
     * lower case
     * 
     * eMail &gt; eMail firstName &gt; firstName URL &gt; URL XAxis &gt; XAxis a &gt; a B &gt; b Yaxis &gt; yaxis
     *
     * @param inputString
     *            the input string
     * @return the valid property name
     */
    public static String getValidPropertyName(String inputString) {
        String answer;

        if (inputString == null) {
            answer = null;
        } else if (inputString.length() < 2) {
            answer = inputString.toLowerCase(Locale.US);
        } else {
            if (Character.isUpperCase(inputString.charAt(0))
                    && !Character.isUpperCase(inputString.charAt(1))) {
                answer = inputString.substring(0, 1).toLowerCase(Locale.US)
                        + inputString.substring(1);
            } else {
                answer = inputString;
            }
        }

        return answer;
    }

    /**
     * Gets the java beans getter.
     *
     * @param introspectedColumn
     *            the introspected column
     * @param context
     *            the context
     * @param introspectedTable
     *            the introspected table
     * @return the java beans getter
     */
    public static Method getJavaBeansGetter(IntrospectedColumn introspectedColumn,
            Context context,
            IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        method.setName(getGetterMethodName(property, fqjt));
        context.getCommentGenerator().addGetterComment(method,
                introspectedTable, introspectedColumn);

        StringBuilder sb = new StringBuilder();
        sb.append("return "); //$NON-NLS-1$
        sb.append(property);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    /**
     * Gets the java beans field.
     *
     * @param introspectedColumn
     *            the introspected column
     * @param context
     *            the context
     * @param introspectedTable
     *            the introspected table
     * @return the java beans field
     */
    public static Field getJavaBeansField(IntrospectedColumn introspectedColumn,
            Context context,
            IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);
        context.getCommentGenerator().addFieldComment(field,
                introspectedTable, introspectedColumn);

        return field;
    }

    /**
     * Gets the java beans setter.
     *
     * @param introspectedColumn
     *            the introspected column
     * @param context
     *            the context
     * @param introspectedTable
     *            the introspected table
     * @return the java beans setter
     */
    public static Method getJavaBeansSetter(IntrospectedColumn introspectedColumn,
            Context context,
            IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getSetterMethodName(property));
        method.addParameter(new Parameter(fqjt, property));
        context.getCommentGenerator().addSetterComment(method,
                introspectedTable, introspectedColumn);

        StringBuilder sb = new StringBuilder();
        if (isTrimStringsEnabled(context) && introspectedColumn.isStringColumn()) {
            sb.append("this."); //$NON-NLS-1$
            sb.append(property);
            sb.append(" = "); //$NON-NLS-1$
            sb.append(property);
            sb.append(" == null ? null : "); //$NON-NLS-1$
            sb.append(property);
            sb.append(".trim();"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        } else {
            sb.append("this."); //$NON-NLS-1$
            sb.append(property);
            sb.append(" = "); //$NON-NLS-1$
            sb.append(property);
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        return method;
    }

    /**
     * 获取toString方法
     * @param type          包括Java类全路径
     * @param topLevelClass 父类
     * @return  解析好的tostring字符串
     */
    public static Method getToStringMethod(FullyQualifiedJavaType type,
                                           TopLevelClass topLevelClass){
        // 添加toString
        // start
        List<Field> fields = topLevelClass.getFields();
        StringBuilder sbType = new StringBuilder();
        StringBuilder sbValue = new StringBuilder();
        sbType.append("return String\n");
        sbType.append("\t\t\t.format(\"" + type.getShortName() + " [");
        for (int j = 0; j < fields.size(); j++) {
            if (j == fields.size() - 1) {
                sbType.append(fields.get(j).getName() + "=%s");
                sbValue.append(fields.get(j).getName() + ");");
            } else {
                sbType.append(fields.get(j).getName() + "=%s,");
                sbValue.append(fields.get(j).getName() + ",");
            }
        }
        sbType.append("]\",\n");
        sbType.append("\t\t\t\t" + sbValue);
        Method toStringMethod = new Method();
        toStringMethod.setVisibility(JavaVisibility.PUBLIC);
        toStringMethod.setName("toString");
        toStringMethod.setReturnType(FullyQualifiedJavaType.getStringInstance());
        toStringMethod.addBodyLine(sbType.toString());
        toStringMethod.addJavaDocLine("@Override");
//        topLevelClass.addMethod(toStringMethod);
        // end
        return toStringMethod;
    }

    /**
     * Checks if is trim strings enabled.
     *
     * @param context
     *            the context
     * @return true, if is trim strings enabled
     */
    private static boolean isTrimStringsEnabled(Context context) {
        Properties properties = context
                .getJavaModelGeneratorConfiguration().getProperties();
        boolean rc = isTrue(properties
                .getProperty(PropertyRegistry.MODEL_GENERATOR_TRIM_STRINGS));
        return rc;
    }

}
