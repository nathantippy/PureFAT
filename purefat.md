#Pure Function Audit Trail

##Overview

Debug and analyse production problems.
Investigate source data problems.

##Features
    * Supports full concurrent usage within one JVM.
    * Assert valid constraints on values at any time. (list assert options)
      Upon failure prints AuditTrail and table?
    * 3 states, Strict, None and the default driven by assertions.
    

##Example

//


##Roadmap 
    
    Unimplemented features needing developers.
    
    Small 
    
        * By design the ringbuffer dynamically grows depending on the usage.
        On startup it takes a few minutes to find the optimium size. Saving
        the optimum size to speed up restarts if the max RAM size did not change.
        
        * Add instance count to each value and consolidate in table report
    
    
    Large
    
        * Use java.lang.instrument.ClassFileTransformer to augment any usages of
        audit() so the expression template and params are injected. The only arguments
        required in the source will be the Number result and a unique label. This
        code will also validate/ensure that the labels are unique.
    
        * Use ToolProvider.getSystemJavaCompiler() to ensure that each label
        is unique. Validating compiler to throw compile error if they are NOT. 
    
        * Full support for concurrent auditing across JVM instances.  Requires 
        serialization mechanism for Numbers and may require log reader.
    
        * logback module for analysis of logged events for stack reconstruction.
        
        * Very low memory implementation that only uses logback.
        
        * mongoDB logback module to support analsys of multiple JVM instances 
        from one place.
        
        * Expression template validating parser.  Critical when template is not 
        injected and will also be used as a test that the injected code is valid.
        Must enforce a simple math BNF that allows Wolfram-alpha, google and other
        tools to parse and evaluate the text. Each template must also be 
        surrounded by () to ensure precedence is maintained in the nesting. 
        Evaluate to ensure the Java code does the same thing as the template.
    
    




Goals and New Features
    /*
     * 
     
     Rules for expression formatting.
     
     Must match slf4j conventions
     
     to make direct insert of values work the * operator must be used in all cases
     if two constants must be multiplied it should be documented as 123*(456) or (132)*(456)*(789)
     apply normal precedence rules
     expressions need to be wrapped in () to ensrue consistency when injecting
     use {} for injection point to match slf4j so it can be leveraged
     
     TODO: use ${myname} for named args in order to support maven, puppet, bash, ant, groovy 
     when requested or when unknown insert these values for interpolation by external tools
     
     
     //TODO: optional, gather call count and params stats min/max/mean/stddev
     
     */

    //TODO: send expected value and get report of the expected inputs to make that happen.
    //TODO: reading byte codes may auto generate the template to greatly simplify the usages.
    
    //  fun.metaData().result    TODO: provides GUID and then line numbers, call perf numbs, param distribution
    //  @attribute may be the best way to inject these identifiers at compile time.
    // or use to confirm the signature of each fun is unique eg (label+text+argCount+constraint) 
    //        just assume unique and as we can afford it confirm by getting line numbers?
