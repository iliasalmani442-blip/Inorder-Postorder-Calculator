//DSA_3-4042023015-Ilia Salmani
package dsa.calculator;
import java.util.Scanner;
import java.util.Stack;
public class DSACalculator{
    static int priority(char op) {
        switch (op) {
            case '+':
            case '-': return 1;
            case '*':
            case '/': return 2;
            case '^': return 3;
            default:  return -1;
        }
    }
    static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }
    static boolean pre(char op1, char op2) {
        return priority(op1) >= priority(op2);
    }
    static long power(long base, long exp) {
        if (exp < 0)
            throw new ArithmeticException("Negative exponent is not supported for integers");
        long result = 1;
        while (exp > 0) {
            if ((exp & 1) == 1) result *= base;
            base *= base;
            exp >>= 1;
        }
        return result;
    }
    static String infixToPostfix(String infix) {
        String s = infix.replaceAll("\\s+", "");
        if (s.isEmpty())
            throw new IllegalArgumentException("Expression is empty");
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        int i = 0, n = s.length();
        boolean expectOperand = true;   
        while (i < n) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (i < n && Character.isDigit(s.charAt(i))) {
                    num.append(s.charAt(i));
                    i++;
                }
                postfix.append(num).append(' ');
                expectOperand = false;
                continue;
            }
            if (c == '(') {
                if (!expectOperand)
                    throw new IllegalArgumentException(
                            "Missing operator before '(' at position " + i);
                stack.push(c);
                expectOperand = true;
                i++;
                continue;
            }
            if (c == ')') {
                if (expectOperand)
                    throw new IllegalArgumentException(
                            "Missing operand before ')' at position " + i);
                boolean found = false;
                while (!stack.isEmpty()) {
                    char top = stack.pop();
                    if (top == '(') { found = true; break; }
                    postfix.append(top).append(' ');
                }
                if (!found)
                    throw new IllegalArgumentException("Unbalanced parentheses");
                expectOperand = false;
                i++;
                continue;
            }
            if (isOperator(c)) {
                if (expectOperand)
                    throw new IllegalArgumentException(
                            "Missing operand before operator '" + c + "' at position " + i);
                while (!stack.isEmpty()
                        && stack.peek() != '('
                        && pre(stack.peek(), c)) {
                    postfix.append(stack.pop()).append(' ');
                }
                stack.push(c);
                expectOperand = true;
                i++;
                continue;
            }
            throw new IllegalArgumentException(
                    "Invalid character '" + c + "' at position " + i);
        }
        if (expectOperand)
            throw new IllegalArgumentException(
                    "Expression ends with an operator or '('");
        while (!stack.isEmpty()) {
            char top = stack.pop();
            if (top == '(')
                throw new IllegalArgumentException("Unbalanced parentheses");
            postfix.append(top).append(' ');
        }
        return postfix.toString().trim();
    }
    static long evaluatePostfix(String postfix) {
        String trimmed = postfix.trim();
        if (trimmed.isEmpty())
            throw new IllegalArgumentException("postorder is empty.");
        Stack<Long> stack = new Stack<>();
        String[] tokens = trimmed.split("\\s+");
        for (String tok : tokens) {
            char c = tok.charAt(0);
            if (tok.length() == 1 && isOperator(c)) {
                if (stack.size() < 2)
                    throw new IllegalArgumentException(
                            "invalid postorder");
                long b = stack.pop();   
                long a = stack.pop();   
                long r;
                switch (c) {
                    case '+': r = a + b; break;
                    case '-': r = a - b; break;
                    case '*': r = a * b; break;
                    case '/':
                        if (b == 0)
                            throw new ArithmeticException("Division by zero");
                        r = a / b;
                        break;
                    case '^':
                        r = power(a, b);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operator: " + c);
                }
                stack.push(r);
            } else {
                try {
                    stack.push(Long.parseLong(tok));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid token: " + tok);
                }
            }
        }
        if (stack.size() != 1)
            throw new IllegalArgumentException(
                    "Invalid postfix expression: extra operand");
        return stack.pop();
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter an infix expression (example: (2+3)*4^2):");
        String infix = sc.nextLine();
        System.out.println("Infix   : " + infix);
        try {
            String postfix = infixToPostfix(infix);
            System.out.println("Postfix : " + postfix);
            long result = evaluatePostfix(postfix);
            System.out.println("Result  : " + result);
        } catch (ArithmeticException e) {
            System.out.println("Arithmetic error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid expression: " + e.getMessage());
        }
        sc.close();
    }
}