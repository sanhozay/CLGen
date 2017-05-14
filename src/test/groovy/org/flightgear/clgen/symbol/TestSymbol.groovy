package org.flightgear.clgen.symbol

import spock.lang.Specification

class TestSymbol extends Specification {

    def "Check that symbols are constructed with a null type"() {
        given:  def symbol = new Symbol("id", "expansion");
        expect: symbol.type == Type.NULL
    }

    def "Check that a new symbol can be converted to a numeric type"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.INT
        expect: symbol.type == Type.INT
    }

    def "Check that a new symbol can be converted to a boolean type"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.BOOL
        expect: symbol.type == Type.BOOL
    }

    def "Check that a new symbol can be converted to a string type"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.STRING
        expect: symbol.type == Type.STRING
    }

    def "Check that an integer symbol can be converted to a double"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.INT
        and:    symbol.type = Type.DOUBLE
        expect: symbol.type == Type.DOUBLE
    }

    def "Check that a double symbol can be converted to an integer"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.DOUBLE
        and:    symbol.type = Type.INT
        expect: symbol.type == Type.INT
    }

    def "Check that a string symbol can be converted to another string"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.STRING
        and:    symbol.type = Type.STRING
        expect: symbol.type == Type.STRING
    }

    def "Check that a boolean symbol can be converted to another boolean"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.BOOL
        and:    symbol.type = Type.BOOL
        expect: symbol.type == Type.BOOL
    }

    def "Check that a numeric symbol cannot be converted to boolean"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.INT
        when:   symbol.type = Type.BOOL
        then:   thrown(TypeException)
    }

    def "Check that a numeric symbol cannot be converted to string"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.INT
        when:   symbol.type = Type.STRING
        then:   thrown(TypeException)
    }

    def "Check that a string symbol cannot be converted to numeric"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.STRING
        when:   symbol.type = Type.INT
        then:   thrown(TypeException)
    }

    def "Check that a boolean symbol cannot be converted to numeric"() {
        given:  def symbol = new Symbol("id", "expansion");
        and:    symbol.type = Type.BOOL
        when:   symbol.type = Type.INT
        then:   thrown(TypeException)
    }

}
