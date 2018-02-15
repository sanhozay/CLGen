package org.flightgear.clgen.symbol

import spock.lang.Specification

class TestSymbolTable extends Specification {

    def "Check that a symbol can be added to and fetched from the symbol table"() {
        given:  def st = new SymbolTable()
        and:    def symbol = new Symbol("t", "testExpanded")
        and:    st.add("Scope", symbol)
        and:    def result = st.lookup("Scope", "t")
        expect: result != null
        and:    result.id == "t"
        and:    result.expansion == "testExpanded"
        and:    result.type == Type.NULL
    }

    def "Check that adding a duplicate symbol throws an exception"() {
        given:  def st = new SymbolTable()
        and:    def symbol = new Symbol("t", "testExpanded")
        and:    st.add("Scope", symbol)
        when:   st.add("Scope", symbol)
        then:   thrown(DuplicateSymbolException)
    }

    def "Check that looking up a symbol returns from the correct scope"() {
        given:  def st = new SymbolTable()
        and:    def symbol = new Symbol("t", "testExpanded")
        and:    st.add("Scope", symbol)
        and:    def other = new Symbol("t", "testOther")
        and:    st.add("Other", other)
        and:    def result = st.lookup("Scope", "t")
        expect: result != null
        and:    result.id == "t"
        and:    result.expansion == "testExpanded"
    }

    def "Check that looking up a missing symbol returns null"() {
        given:  def st = new SymbolTable()
        and:    def symbol = new Symbol("t", "testExpanded")
        and:    st.add("Scope", symbol)
        expect: st.lookup("Scope", "u") == null
    }

    def "Check that looking up a missing symbol respects scope"() {
        given:  def st = new SymbolTable()
        and:    def symbol = new Symbol("t", "testExpanded")
        and:    st.add("Scope", symbol)
        expect: st.lookup("Other", "t") == null
    }

    def "Check that symbols can be retrieved from global scope"() {
        given:  def st = new SymbolTable()
        and:    def symbol = new Symbol("t", "testExpanded")
        and:    st.add(SymbolTable.GLOBAL, symbol)
        and:    def result = st.lookup(SymbolTable.GLOBAL, "t")
        expect: result != null
        and:    result.id == "t"
        and:    result.expansion == "testExpanded"
    }

    def "Check that local scope has precedence over global scope"() {
        given:  def st = new SymbolTable()
        and:    def global = new Symbol("t", "testGlobal")
        and:    st.add(SymbolTable.GLOBAL, global)
        and:    def local = new Symbol("t", "testLocal")
        and:    st.add("Local", local)
        and:    def result = st.lookup("Local", "t")
        expect: result != null
        and:    result.id == "t"
        and:    result.expansion == "testLocal"
    }

        def "Check that global scope is used if missing in local"() {
        given:  def st = new SymbolTable()
        and:    def global = new Symbol("t", "testGlobal")
        and:    st.add(SymbolTable.GLOBAL, global)
        and:    def result = st.lookup("Local", "t")
        expect: result != null
        and:    result.id == "t"
        and:    result.expansion == "testGlobal"
    }
}
