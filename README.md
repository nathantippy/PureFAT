PureFAT
=======
pure function audit trail for Java 

NOTE: Under active development so much of this is subject to change.


Overview
---------

Helps investigate the source of unexpected numeric values.  Data about each 
value is record from the point that values are sampled from feeds or sensors 
all the way to the end where rolled up summaries are displayed in a report.

In the event that a "clearly wrong" value is detected in the report and audit
trail can be generated which will show from original inputs how that value 
was derived.  The audit trail provides something that resembles a stack trace
so the investigator can look at each line of code that contributed to the value
under investigation.

Working Features
----------------

    # Full concurrent usage within one JVM. The audit can span threads.
    # Supports disabled, full, and assert modes of operation.
    # Supports many constraint rules for throwing upon "bad data"
    # Supports logging trace as needed for "questionable data"
    # Supports internal memory mode or external slf4j mode or both.
    # Audit can be printed as detailed table, summary or tree.

Example
------

    See Unit tests (still under construction)
    MotorRPMUseCase attempts to capture how one might expect to use it.
    
    Here are the 4 lines of code corresponding to the audit trail below:

          private final Integer samplesPerSecond = audit(1024,"samplesPerSecond");
          private final Integer samplesPerMinute = audit(60*samplesPerSecond,"samplesPerMinute","60*{}",samplesPerSecond);
          Integer samplesPerRevolution = audit(count*2,"samplesPerRevolution","({}*2)",count);
          rpm = audit(samplesPerMinute/(double)samplesPerRevolution,"rpm","({}/{})",samplesPerMinute,samplesPerRevolution);
    
    Here is an example audit trail after an Infinity was detected.
    
          1024     = 1024       com.ociweb.purefat.useCase.MotorRPMUseCase.<init>(MotorRPMUseCase.java:46)          samplesPerSecond[PF1c466919]                                                     
          61440    = 60*1024    com.ociweb.purefat.useCase.MotorRPMUseCase.<init>(MotorRPMUseCase.java:47)          samplesPerMinute[PF529df6cf] = 60*samplesPerSecond[PF1c466919]                   
          0        = (0*2)      com.ociweb.purefat.useCase.MotorRPMUseCase.computeResult(MotorRPMUseCase.java:106)  samplesPerRevolution[PFb6ba69] = (initial[PFb6ba69]*2)                           
          Infinity = (61440/0)  com.ociweb.purefat.useCase.MotorRPMUseCase.computeResult(MotorRPMUseCase.java:107)  rpm[PF5c8843dc] = (samplesPerMinute[PF529df6cf]/samplesPerRevolution[PF761f4ff9])


Roadmap 
-------

    Unimplemented features needing developers.

        * By design the ringbuffer dynamically grows depending on the usage.
        On startup it takes a few minutes to find the optimium size. Save
        the optimum size to speed up restarts if the max RAM size did not change.
    
        * Use java.lang.instrument.ClassFileTransformer to augment any usages of
        audit() so the expression template and params are injected. The only arguments
        required in the source will be the Number result and a unique label. This
        code will also validate/ensure that the labels are unique.
    
        * Use ToolProvider.getSystemJavaCompiler() to ensure that each label
        is unique. Validating compiler to throw compile error if they are NOT. 
    
        * logback module for analysis of logged events for stack reconstruction
        off line.
        
        * mongoDB logback module to support analysis of multiple JVM instances 
        from one place.
        
        * Expression template validating parser.  Critical when template is not 
        injected and will also be used as a test that the injected code is valid.
        Must enforce a simple math BNF that allows Wolfram-alpha, google and other
        tools to parse and evaluate the text. Each template must also be 
        surrounded by () to ensure precedence is maintained in the nesting. 
        Evaluate to ensure the Java code does the same thing as the template.
    
        * Gather more meta data about each audited function.  Values like the 
        min, max, mean, and standard deviation of the results may be helpful.

        * Add mode to use ${myname} for named args in order to better support 
        maven, puppet, bash, ant, groovy  when requested or when unknown. 
        Insert these tags for interpolation by external tools.


Expression formatting
---------------------

     To make nesting of expressions work work the * operator must be used in all cases for multiplication.
     Expressions need to be wrapped in () to ensue consistency when injecting
     Always use {} for injection point to match slf4j conventions


