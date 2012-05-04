#!/bin/sh

mvn clean install -PccProfile
mvn sonar:sonar -PccProfile


