package com.example.messagingstompwebsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import java.util.*;
@Controller
public class GreetingController {
	private static final Map<Character, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
	static {
		OPERATOR_PRECEDENCE.put('^', 3);
		OPERATOR_PRECEDENCE.put('*', 2);
		OPERATOR_PRECEDENCE.put('/', 2);
		OPERATOR_PRECEDENCE.put('+', 1);
		OPERATOR_PRECEDENCE.put('-', 1);
	}

	public static String infixToPostfix(String expression) {
		Deque<Character> operatorStack = new ArrayDeque<>();
		StringBuilder postfixBuilder = new StringBuilder();
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if (Character.isDigit(c)) {
				postfixBuilder.append(c);
			} else if (c == '(') {
				operatorStack.push(c);
			} else if (c == ')') {
				while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
					postfixBuilder.append(operatorStack.pop());
				}
				if (!operatorStack.isEmpty() && operatorStack.peek() == '(') {
					operatorStack.pop();
				}
			} else if (OPERATOR_PRECEDENCE.containsKey(c)) {
				while (!operatorStack.isEmpty() && operatorStack.peek() != '(' &&
						OPERATOR_PRECEDENCE.get(c) <= OPERATOR_PRECEDENCE.get(operatorStack.peek())) {
					postfixBuilder.append(operatorStack.pop());
				}
				operatorStack.push(c);
			}
		}
		while (!operatorStack.isEmpty()) {
			postfixBuilder.append(operatorStack.pop());
		}
		return postfixBuilder.toString();
	}
	public static String infixToPrefix(String expression) {
		String reversedExpression = new StringBuilder(expression).reverse().toString();
		Deque<Character> operatorStack = new ArrayDeque<>();
		StringBuilder prefixBuilder = new StringBuilder();
		for (int i = 0; i < reversedExpression.length(); i++) {
			char c = reversedExpression.charAt(i);
			if (Character.isDigit(c)) {
				prefixBuilder.append(c);
			} else if (c == ')') {
				operatorStack.push(c);
			} else if (c == '(') {
				while (!operatorStack.isEmpty() && operatorStack.peek() != ')') {
					prefixBuilder.append(operatorStack.pop());
				}
				if (!operatorStack.isEmpty() && operatorStack.peek() == ')') {
					operatorStack.pop();
				}
			} else if (OPERATOR_PRECEDENCE.containsKey(c)) {
				while (!operatorStack.isEmpty() && operatorStack.peek() != ')' &&
						OPERATOR_PRECEDENCE.get(c) < OPERATOR_PRECEDENCE.get(operatorStack.peek())) {
					prefixBuilder.append(operatorStack.pop());
				}
				operatorStack.push(c);
			}
		}
		while (!operatorStack.isEmpty()) {
			prefixBuilder.append(operatorStack.pop());
		}
		return prefixBuilder.reverse().toString();
	}
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(UserMessage message) throws Exception {
		String s = HtmlUtils.htmlEscape(message.getNum1());
		String postfix = infixToPostfix(s);
		String prefix = infixToPrefix(s);
		if(prefix.length()==0 && postfix.length()==0){
			return new Greeting("Given Input String is not a valid Infix Expression");
		}
		else{
			return new Greeting("Prefix is "+prefix+", Postfix is "+postfix);

		}
	}

}
