package com.company.core;


import com.company.annotation.*;

public final class Constant {
    protected static final Class<ComponentScan> packageAnnotation = ComponentScan.class;
    protected static final Class<Component> initAnnotation = Component.class;
    protected static final Class<Configuration> configurationAnnotation = Configuration.class;
    protected static final Class<Qualifier> qualifierAnnotation = Qualifier.class;
    protected static final Class<Value> valueAnnotation = Value.class;
    protected static final Class<Autowired> INITIALIZATION_ANNOTATION = Autowired.class;
    protected static final Class<PostConstruct> POST_CONSTRUCT_ANNOTATION = PostConstruct.class;
    protected static final Class<PreDestroy> PRE_DESTROY_ANNOTATION = PreDestroy.class;
    protected static final Class<Lazy> LAZY = Lazy.class;


}
