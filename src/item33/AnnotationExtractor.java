package com.khc.practice.effectivejava.ch05.item33;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationExtractor {
    static Annotation getAnnotation(AnnotatedElement element, String annotationTypedName){
        Class<?> annotationType = null;
        try {
            annotationType = Class.forName(annotationTypedName);
        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }

        return element.getAnnotation(
                annotationType.asSubclass(Annotation.class)
        );
    }

    @Anno
    public int method(){
        return 0;
    }
    public static void main(String[] args) throws Exception {

        AnnotationExtractor extractor = new AnnotationExtractor();

        AnnotatedElement element = extractor.getClass().getMethod("method");
        Annotation annotation = getAnnotation(element, "com.khc.practice.effectivejava.ch05.item33.Anno");
        System.out.println(annotation.annotationType().getName());
    }
}
