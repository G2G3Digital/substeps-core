package com.technophobia.substeps.runner.runtime;

import java.util.Iterator;

public interface ClassLocator {

	Iterator<Class<?>> fromPath(String path);
}
