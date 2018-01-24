package org.flightgear.clgen.listener

import spock.lang.Specification

class TestAbstractListener extends Specification {

    class Listener extends AbstractListener {}

    def listener = new Listener()

    def "Check simple quoted strings can be unquoted"() {
        expect: listener.unquote('"abc"') == 'abc'
        and:    listener.unquote('"a"') == 'a'
        and:    listener.unquote('""') == ''
    }

    def "Check quoted strings containing quotes can be unquoted"() {
        expect: listener.unquote('"ab"c"') == 'ab"c'
    }

    def "Check unquoting a null string throws an exception"() {
        when:   listener.unquote(null)
        then:   thrown(NullPointerException)
    }

    def "Check unquoting an unquoted string throws an exception"() {
        when:   listener.unquote('abc')
        then:   thrown(IllegalArgumentException)
        when:   listener.unquote('abc"')
        then:   thrown(IllegalArgumentException)
        when:   listener.unquote('"abc')
        then:   thrown(IllegalArgumentException)
        when:   listener.unquote('a"bc')
        then:   thrown(IllegalArgumentException)
    }

    def "Check quoted strings with escapes are unquoted"() {
        expect: listener.unquote(/"abc\""/) == /abc"/
        and:    listener.unquote('"ab\\c"') == 'ab\\c'
    }

}
