package zhenfei.liu.config;

import open.fegin.annotation.MyFeginClient;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import zhenfei.liu.MyFeignClientFactoryBean;
import zhenfei.liu.annotation.EnableMyFeginClients;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lzf
 * desc
 * date 2019/11/22-16:53
 */
public class EnableMyFeginClientsConfiguration implements ImportBeanDefinitionRegistrar,ResourceLoaderAware, BeanClassLoaderAware {

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public EnableMyFeginClientsConfiguration() {
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }



    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        Set<String> basePackages;
        Map<String, Object> attrs = metadata
                .getAnnotationAttributes(EnableMyFeginClients.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                MyFeginClient.class);
        final Class<?>[] clients = attrs == null ? null
                : (Class<?>[]) attrs.get("clients");
        if (clients == null || clients.length == 0) {
            scanner.addIncludeFilter(annotationTypeFilter);
            basePackages = getBasePackages(metadata);
        }
        else {
            final Set<String> clientClasses = new HashSet<>();
            basePackages = new HashSet<>();
            for (Class<?> clazz : clients) {
                basePackages.add(ClassUtils.getPackageName(clazz));
                clientClasses.add(clazz.getCanonicalName());
            }
            AbstractClassTestingTypeFilter filter = new AbstractClassTestingTypeFilter() {
                @Override
                protected boolean match(ClassMetadata metadata) {
                    String cleaned = metadata.getClassName().replaceAll("\\$", ".");
                    return clientClasses.contains(cleaned);
                }
            };
            scanner.addIncludeFilter(annotationTypeFilter);
        }
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@MyFeginClient can only be specified on an interface");

                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(
                                    MyFeginClient.class.getCanonicalName());

                    Set<MethodMetadata> methodMetadata =  annotationMetadata.getAnnotatedMethods(RequestMapping.class.getCanonicalName());
                    Class clazz = null;
                    for(MethodMetadata metadata1 : methodMetadata){
                        try{
                             clazz =  Class.forName(metadata1.getDeclaringClassName());
                        }catch (ClassNotFoundException e){
                            throw new RuntimeException(metadata1.getDeclaringClassName() +":class not found");
                        }
                        attributes.put("path",metadata1.getMethodName());
                        attributes.put("serverInterface",clazz);
                        AnnotationAttributes annotationAttributes = metadata1.getAnnotations().get(RequestMapping.class).asAnnotationAttributes();
                        Assert.notNull(annotationAttributes,"MyRequestMapping must be is exist");
                    }
                    registerMyFeignClient(registry, annotationMetadata, attributes);
                }
            }
        }
    }


    private void registerMyFeignClient(BeanDefinitionRegistry registry,AnnotationMetadata metadata, Map<String, Object> attributes){
        String className = metadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(MyFeignClientFactoryBean.class);
        definition.addPropertyValue("type",className);
        String alias = className;
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        String qualifier = getQualifier(attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] { alias });
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private void registerDefualtConfig(BeanDefinitionRegistry registry){

    }


    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {

            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    // TODO until SPR-11711 will be resolved
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition
                            .getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(
                                    beanDefinition.getMetadata().getClassName(),
                                    EnableMyFeginClientsConfiguration.this.classLoader);
                            return !target.isAnnotation();
                        }
                        catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: "
                                            + beanDefinition.getMetadata().getClassName(),
                                    ex);

                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableMyFeginClients.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }
}
