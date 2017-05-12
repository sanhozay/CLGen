package org.flightgear.clgen.ast;

import spock.lang.Specification;

class TestChecklist extends Specification {

    def "Check that adding a check to a checklist creates a page"() {
        given:  def checklist = new Checklist("title")
        and:    def check = Mock(Check, constructorArgs: [Mock(Item), Mock(State)])
        when:   checklist.addCheck(check)
        then:   checklist.pages.size() == 1
    }

    def "Check that adding two checks to a checklist uses one page"() {
        given:  def checklist = new Checklist("title")
        and:    def check1 = Mock(Check, constructorArgs: [Mock(Item), Mock(State)])
        and:    def check2 = Mock(Check, constructorArgs: [Mock(Item), Mock(State)])
        when:   checklist.addCheck(check1)
        then:   checklist.pages.size() == 1
        when:   checklist.addCheck(check2)
        then:   checklist.pages.size() == 1
    }

}
