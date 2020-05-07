# npm_vs_oss-index

## Motivation

* First of, this is not criticism on [OSS Index](https://ossindex.sonatype.org/), in fact I want to thank
  Sonatype for providing this excellent service for free!
* When analyzing vulnerabilities in JS packages i noticed that OSS Index does not correctly process the
  version identifiers provided by the npm advisories.
* In tools like [Dependency-Track](https://dependencytrack.org/) where npm audit and OSS Index are both used
  this leeds to additional manual audit work. And every additional work needed lowers acceptance of security
  tools thus hurting security.
* Therefore I decided to investigate the differences between the two systems a bit more closely.
* I also want to thank the JS community for providing the npm advisories. Big contribution to a more secure
  JS ecosystem.

## What it does

* **Step1**: fetch advisory data from [npmjs](https://www.npmjs.com/advisories/) and store it locally
  * Runtime ~2 hours
* **Step2**: obtain reports for every found package and version from Step1 from OSS Index
  * Runtime ~4 hours
  * You will hit the request rate limit anyway, so provide a username and API token, see below

## How to run

* main needs 1 command line argument: `Step1`, `Step2` or `Step3`
* `Step2` needs a username and API token, provided as system properties.
  * `-Dio.github.scolytus.token=$TOKEN -Dio.github.scolytus.user=$YOURMAIL`

```
$ ./mvnw clean package
$
$ java -jar target/npm-vs-ossindex-1.0-SNAPSHOT-jar-with-dependencies.jar Step1
$
$ java -jar target/npm-vs-ossindex-1.0-SNAPSHOT-jar-with-dependencies.jar \
        -Dio.github.scolytus.token=$TOKEN \
        -Dio.github.scolytus.user=$YOURMAIL \
        Step2
```

## Notes

* poor code quality, this was kind of scripted together
* I wanted to speed up my "scripting in Java", still too slow, Python would be way faster in development :-(
  * But hey, I'm a Java dev, don't be afraid to use Java also for scripting - just accept the pain :-)
