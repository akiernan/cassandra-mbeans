/*
 * This Spock specification was auto generated by running the Gradle 'init' task
 * by 'alex.kiernan' at '12/20/16 10:15 AM' with Gradle 3.2.1
 *
 * @author alex.kiernan, @date 12/20/16 10:15 AM
 */

import spock.lang.Specification

class LibraryTest extends Specification{
    def "someLibraryMethod returns true"() {
        setup:
        Library lib = new Library()
        when:
        def result = lib.someLibraryMethod()
        then:
        result == true
    }
}