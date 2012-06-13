package com.technophobia.substeps.runner.runtime.fake;

import com.technophobia.substeps.model.SubSteps.Step;

public class AnnotatedStepClassFake {

	@Step("A Step")
	public void stepMethod(){
		// no-op
	}
}
