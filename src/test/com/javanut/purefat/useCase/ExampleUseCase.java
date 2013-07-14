package com.javanut.purefat.useCase;

import java.util.Iterator;

public interface ExampleUseCase {

    Iterator<Number> samples();

    int samplesCount();

    Number computeResult(Number sample);

    void validatResult(Number result);

}
