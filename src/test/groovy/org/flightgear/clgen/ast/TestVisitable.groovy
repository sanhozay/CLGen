package org.flightgear.clgen.ast;

import spock.lang.Specification;

class TestVisitable extends Specification {

    def visitor = Mock(Visitor)

    def "Check that an abstract syntax tree accepts its visitor"() {
        given:  def checklist = Mock(Checklist)
        and:    def domain = new AbstractSyntaxTree()
        and:    domain.addChecklist(checklist)
        when:   domain.accept(visitor)
        then:   1 * visitor.enter(domain)
        and:    1 * checklist.accept(visitor)
        and:    1 * visitor.exit(domain)
    }

    def "Check that a binary expression accepts its visitor"() {
        given:  def expression = new BinaryCondition(Operator.AND)
        and:    def lhs = Mock(Condition);
        and:    def rhs = Mock(Condition);
        and:    expression.addChild(lhs)
        and:    expression.addChild(rhs)
        when:   expression.accept(visitor)
        then:   1 * visitor.enter(expression)
        and:    1 * lhs.accept(visitor)
        and:    1 * rhs.accept(visitor)
        and:    1 * visitor.exit(expression)
    }

    def "Check that a check accepts its visitor"() {
        given:  def item = Mock(Item);
        and:    def state = Mock(State);
        and:    def check = new Check(item, state);
        when:   check.accept(visitor)
        then:   1 * visitor.enter(check)
        and:    1 * state.accept(visitor)
        and:    1 * item.accept(visitor)
        and:    1 * visitor.exit(check)
    }

    def "Check that an item accepts its visitor"() {
        given:  def item = new Item();
        and:    def coord = Mock(Coordinate, constructorArgs: [0, 0, 0])
        and:    def marker = Mock(Marker, constructorArgs: [coord, 0])
        and:    item.marker = marker
        when:   item.accept(visitor)
        then:   1 * visitor.enter(item)
        and:    1 * marker.accept(visitor)
        and:    1 * visitor.exit(item)
    }

    def "Check that a checklist accepts its visitor"() {
        given:  def page = Mock(Page)
        and:    def checklist = new Checklist("title")
        and:    checklist.addPage(page)
        when:   checklist.accept(visitor)
        then:   1 * visitor.enter(checklist)
        and:    1 * page.accept(visitor)
        and:    1 * visitor.exit(checklist)
    }

    def "Check that a command binding accepts its visitor"() {
        given:  def binding = new CommandBinding("some-command")
        and:    def condition = Mock(Condition);
        and:    binding.condition = condition
        when:   binding.accept(visitor)
        then:   1 * visitor.enter(binding)
        and:    1 * condition.accept(visitor)
        and:    1 * visitor.exit(binding)
    }

    def "Check that a condition accepts its visitor"() {
        given:  def condition = new Condition()
        and:    def child = Mock(Condition)
        and:    condition.addChild(child)
        when:   condition.accept(visitor)
        then:   1 * visitor.enter(condition)
        and:    1 * child.accept(visitor)
        and:    1 * visitor.exit(condition)
    }

    def "Check that a marker accepts its visitor"() {
        given: def marker = new Marker(null, 0)
        when: marker.accept(visitor)
        then: 1 * visitor.enter(marker)
        then: 1 * visitor.exit(marker)
    }

    def "Check that a page accepts its visitor"() {
        given:  def check = Mock(Check, constructorArgs: [Mock(Item), Mock(State)])
        and:    def page = new Page()
        and:    page.addCheck(check)
        when:   page.accept(visitor)
        then:   1 * visitor.enter(page)
        and:    1 * check.accept(visitor)
        and:    1 * visitor.exit(page)
    }

    def "Check that a property binding accepts its visitor"() {
        given:  def binding = new PropertyBinding('some/property', 'some/other')
        and:    def condition = Mock(Condition);
        and:    binding.condition = condition
        when:   binding.accept(visitor)
        then:   1 * visitor.enter(binding)
        and:    1 * condition.accept(visitor)
        and:    1 * visitor.exit(binding)
    }

    def "Check that a state accepts its visitor"() {
        given:  def state = new State('name')
        and:    def condition = Mock(Condition)
        and:    state.condition = condition
        and:    def binding = Mock(ValueBinding, constructorArgs: ['some/property', null])
        and:    state.addBinding(binding)
        when:   state.accept(visitor)
        then:   1 * visitor.enter(state)
        and:    1 * condition.accept(visitor)
        and:    1 * binding.accept(visitor)
        and:    1 * visitor.exit(state)
    }

    def "Check that a terminal accepts its visitor"() {
        given:  def terminal = new Terminal(TerminalType.DOUBLE, null)
        and:    def condition = Mock(Condition);
        when:   terminal.accept(visitor)
        then:   1 * visitor.enter(terminal)
        and:    1 * visitor.exit(terminal)
    }

    def "Check that a unary expression accepts its visitor"() {
        given:  def expression = new UnaryCondition(Operator.NOT)
        and:    def condition = Mock(Condition);
        and:    expression.addChild(condition)
        when:   expression.accept(visitor)
        then:   1 * visitor.enter(expression)
        and:    1 * condition.accept(visitor)
        and:    1 * visitor.exit(expression)
    }

    def "Check that a value binding accepts its visitor"() {
        given:  def binding = new ValueBinding("some/property", 0.0)
        and:    def condition = Mock(Condition);
        and:    binding.condition = condition
        when:   binding.accept(visitor)
        then:   1 * visitor.enter(binding)
        and:    1 * condition.accept(visitor)
        and:    1 * visitor.exit(binding)
    }

}
