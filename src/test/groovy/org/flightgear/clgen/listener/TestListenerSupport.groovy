package org.flightgear.clgen.listener;

import static org.flightgear.clgen.listener.ListenerSupport.unquote

import spock.lang.Specification;

class TestListenerSupport extends Specification {

    def "Check simple quoted strings can be unquoted"() {
        expect: unquote('"abc"') == 'abc'
        and:    unquote('"a"') == 'a'
        and:    unquote('""') == ''
    }

    def "Check quoted strings containing quotes can be unquoted"() {
        expect: unquote('"ab"c"') == 'ab"c'
    }

    def "Check unquoting a null string throws an exception"() {
        when:   unquote(null)
        then:   thrown(NullPointerException)
    }

    def "Check unquoting an unquoted string throws an exception"() {
        when:   unquote('abc')
        then:   thrown(IllegalArgumentException)
        when:   unquote('abc"')
        then:   thrown(IllegalArgumentException)
        when:   unquote('"abc')
        then:   thrown(IllegalArgumentException)
        when:   unquote('a"bc')
        then:   thrown(IllegalArgumentException)
    }

    def "Check quoted strings with escapes are unquoted"() {
        expect: unquote(/"abc\""/) == /abc"/
        and:    unquote('"ab\\c"') == 'ab\\c'
    }

}
