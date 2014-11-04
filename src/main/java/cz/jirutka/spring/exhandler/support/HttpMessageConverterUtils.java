/*
 * Copyright 2014 Jakub Jirutka <jakub@jirutka.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.jirutka.spring.exhandler.support;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.ClassUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class HttpMessageConverterUtils {

    private static final ClassLoader CLASSLOADER = HttpMessageConverterUtils.class.getClassLoader();

    /**
     * Determine whether a JAXB binder is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@link javax.xml.bind.Binder} or one of its dependencies is not
     * present or cannot be loaded.
     */
    public static boolean isJaxb2Present() {
        return ClassUtils.isPresent("javax.xml.bind.Binder", CLASSLOADER);
    }

    /**
     * Determine whether Jackson 2.x is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@link com.fasterxml.jackson.databind.ObjectMapper},
     * {@link com.fasterxml.jackson.core.JsonGenerator} or one of its dependencies is not present
     * or cannot be loaded.
     */
    public static boolean isJackson2Present() {
        return ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", CLASSLOADER) &&
                ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", CLASSLOADER);
    }

    /**
     * Determine whether Jackson 1.x is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@link org.codehaus.jackson.map.ObjectMapper},
     * {@link org.codehaus.jackson.JsonGenerator} or one of its dependencies is not present or
     * cannot be loaded.
     */
    public static boolean isJacksonPresent() {
        return ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", CLASSLOADER) &&
                ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", CLASSLOADER);
    }

    /**
     * Determine whether a fastjson is present on the classpath and can be loaded. Will return
     * <tt>false</tt> if either the {@link com.alibaba.fastjson.JSON} or one of its dependencies is not
     * present or cannot be loaded.
     * @return
     */
    public static boolean isFastJsonPresent() {
        return ClassUtils.isPresent("com.alibaba.fastjson.JSON", CLASSLOADER);
    }

    /**
     * Returns default {@link HttpMessageConverter} instances, i.e.:
     *
     * <ul>
     *     <li>{@linkplain ByteArrayHttpMessageConverter}</li>
     *     <li>{@linkplain StringHttpMessageConverter}</li>
     *     <li>{@linkplain ResourceHttpMessageConverter}</li>
     *     <li>{@linkplain Jaxb2RootElementHttpMessageConverter} (when JAXB is present)</li>
     *     <li>{@linkplain MappingJackson2HttpMessageConverter} (when Jackson 2.x is present)</li>
     *     <li>{@linkplain FastJsonHttpMessageConverter} (when Fastjson is present)</li>
     *     <li>{@linkplain MappingJacksonHttpMessageConverter} (when Jackson 1.x is present and 2.x not)</li>
     * </ul>
     *
     * <p>Note: It does not return all of the default converters defined in Spring, but just thus
     * usable for exception responses.</p>
     */
    @SuppressWarnings("deprecation")
    public static List<HttpMessageConverter<?>> getDefaultHttpMessageConverters() {

        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setWriteAcceptCharset(false); // See SPR-7316

        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());

        if (isJaxb2Present()) {
            converters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (isJackson2Present()) {
            converters.add(new MappingJackson2HttpMessageConverter());

        } else if(isFastJsonPresent()) {
            converters.add(new FastJsonHttpMessageConverter());
        } else if (isJacksonPresent()) {
            converters.add(new MappingJacksonHttpMessageConverter());
        }
        return converters;
    }
}
