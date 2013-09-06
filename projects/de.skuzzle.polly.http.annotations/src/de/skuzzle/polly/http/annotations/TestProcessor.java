package de.skuzzle.polly.http.annotations;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes({
    "de.skuzzle.polly.http.annotations.RequestHandler"
})
public class TestProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return this.processingEnv.getSourceVersion();
    }
    

    
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        
        final Set<? extends Element> getElements = roundEnv.getElementsAnnotatedWith(Get.class);
        final Set<? extends Element> postElements = roundEnv.getElementsAnnotatedWith(Post.class);
        final Set<? extends Element> rh = roundEnv.getElementsAnnotatedWith(RequestHandler.class);
        this.processingEnv.getMessager().printMessage(Kind.WARNING, "WTF?");
        for (final Element e : rh) {
            this.processingEnv.getMessager().printMessage(Kind.NOTE, "Hi,  I'm a GET element", e);
        }
        return true;
    }

}
