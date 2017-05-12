package org.flightgear.clgen.backend;

import spock.lang.Specification;

class TestXmlPostProcessor extends Specification {

    def "Check that post processing does not affect simple tags"() {
        given:  def xpp = new XmlPostProcessor()
        and:    xpp.xml = '<tag1>\n<tag2>content</tag2>\n</tag1>\n'
        expect: xpp.xml == '<tag1>\n<tag2>content</tag2>\n</tag1>\n'
    }

    def "Check that post processing does not add extra newlines"() {
        given:  def xpp = new XmlPostProcessor()
        and:    xpp.xml = '<tag1><tag2>content</tag2></tag1>'
        expect: xpp.xml == '<tag1><tag2>content</tag2></tag1>'
    }

    def "Check that post processing breaks lines with comment and new tag"() {
        given:  def xpp = new XmlPostProcessor()
        and:    xpp.xml = '<!-- comment --><tag></tag>'
        expect: xpp.xml == '<!-- comment -->\n<tag></tag>'
    }

    def "Check that post processing adds blank lines after spaced tags"() {
        given:  def xpp = new XmlPostProcessor()
        and:    xpp.addBreakPatterns('<tag1>')
        and:    xpp.xml = '<tag1>\n<tag2>content</tag2>\n</tag1>\n'
        expect: xpp.xml == '<tag1>\n\n<tag2>content</tag2>\n</tag1>\n'
    }

}
