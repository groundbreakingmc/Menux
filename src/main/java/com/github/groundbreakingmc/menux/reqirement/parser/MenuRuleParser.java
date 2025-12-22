package com.github.groundbreakingmc.menux.reqirement.parser;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.reqirement.condition.MenuCondition;
import com.github.groundbreakingmc.menux.reqirement.condition.impl.basic.*;
import com.github.groundbreakingmc.menux.reqirement.condition.impl.logic.AndCondition;
import com.github.groundbreakingmc.menux.reqirement.condition.impl.logic.NotCondition;
import com.github.groundbreakingmc.menux.reqirement.condition.impl.logic.OrCondition;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import com.github.groundbreakingmc.menux.reqirement.value.ValueProvider;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MenuRuleParser {

    private final MenuRuleParserOptions options;
    private final String rawCondition;
    private final char[] chars;
    private final List<MenuAction> denyActions;
    private int pos;

    private MenuRuleParser(MenuRuleParserOptions options,
                           String rawCondition,
                           List<MenuAction> denyActions) {
        this.options = options;
        this.rawCondition = rawCondition;
        this.denyActions = ImmutableList.copyOf(denyActions);
        this.chars = rawCondition.toCharArray();
        this.pos = 0;
    }

    public static MenuRule parse(@NotNull MenuRuleParserOptions options,
                                 @NotNull String rawCondition,
                                 @NotNull List<MenuAction> denyActions) {
        return new MenuRuleParser(options, rawCondition.trim(), denyActions).parse();
    }

    public MenuRule parse() {
        this.skipWhitespace();
        final MenuCondition condition = this.parseOr();
        if (this.pos < this.chars.length) {
            throw new IllegalArgumentException("Unexpected characters '" + this.collectRest() + "' at position " + this.pos);
        }
        return new MenuRule(condition, this.denyActions);
    }

    private MenuCondition parseOr() {
        MenuCondition left = this.parseAnd();

        while (this.match(this.options.orIdentifier())) {
            skipWhitespace();
            final MenuCondition right = parseAnd();
            left = new OrCondition(left, right);
        }

        return left;
    }

    private MenuCondition parseAnd() {
        MenuCondition left = this.parseUnary();

        while (this.match(this.options.andIdentifier())) {
            this.skipWhitespace();
            final MenuCondition right = this.parseUnary();
            left = new AndCondition(left, right);
        }

        return left;
    }

    private MenuCondition parseUnary() {
        this.skipWhitespace();

        if (this.match(this.options.notIdentifier())) {
            this.skipWhitespace(); // TODO check possible infinity loop
            return new NotCondition(this.parseUnary());
        }

        return this.parsePrimary();
    }

    private MenuCondition parsePrimary() {
        this.skipWhitespace();

        if (this.match("(")) {
            this.skipWhitespace();
            final MenuCondition condition = this.parseOr();
            this.skipWhitespace();
            this.expect(")");
            return condition;
        }

        return this.parseConditionOrComparison();
    }

    private MenuCondition parseConditionOrComparison() {
        final ValueProvider left = this.parseExpression();
        this.skipWhitespace();

        if (this.match("==")) {
            this.skipWhitespace();
            final ValueProvider right = this.parseExpression();
            return new EqualsCondition(left, right);
        }

        if (this.match("!=")) {
            this.skipWhitespace();
            final ValueProvider right = this.parseExpression();
            return new NotEqualsCondition(left, right);
        }

        if (this.match(">=")) {
            this.skipWhitespace();
            final ValueProvider right = this.parseExpression();
            return new GreaterThanOrEqualCondition(left, right);
        }

        if (this.match("<=")) {
            this.skipWhitespace();
            final ValueProvider right = this.parseExpression();
            return new LessThanOrEqualCondition(left, right);
        }

        if (this.match(">")) {
            this.skipWhitespace();
            final ValueProvider right = this.parseExpression();
            return new GreaterThanCondition(left, right);
        }

        if (this.match("<")) {
            this.skipWhitespace();
            final ValueProvider right = this.parseExpression();
            return new LessThanCondition(left, right);
        }

        return new BooleanCondition(left);
    }

    private ValueProvider parseExpression() {
        this.skipWhitespace();

        final char peek = this.peek();

        if (peek == '"' || peek == '\'') {
            final String str = this.parseString();
            return ctx -> str;
        }

        if (peek == '-' || Character.isDigit(peek)) {
            final Number num = this.parseNumber();
            return ctx -> num;
        }

        if (peek == '%') {
            this.consume();
            final String placeholder = '%' + this.parseIdentifier() + '%';
            this.expect("%");
            return ctx -> ctx.menuInst().placeholderParser().parse(ctx.player(), placeholder);
        }

        final String identifier = this.parseIdentifier();
        this.skipWhitespace();

        if (this.peek() == '(') {
            return this.parseFunctionCall(identifier);
        }

        return this.parseVariableOrFunction(identifier);
    }

    private ValueProvider parseFunctionCall(String functionName) {
        this.consume();

        final List<ValueProvider> arguments = new ObjectArrayList<>();
        this.skipWhitespace();

        if (this.peek() != ')') {
            while (true) {
                arguments.add(this.parseExpression());
                this.skipWhitespace();

                if (this.peek() == ')') {
                    break;
                }

                this.expect(",");
                this.skipWhitespace();
            }
        }

        this.expect(")");

        final var functionDef = this.options.functions().get(functionName);
        if (functionDef == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }

        return functionDef.create(arguments);
    }

    private ValueProvider parseVariableOrFunction(String identifier) {
        final var functionDef = this.options.functions().get(identifier);
        if (functionDef != null) {
            return functionDef.create(List.of());
        }

        final var variableDef = this.options.variables().get(identifier);
        if (variableDef != null) {
            return variableDef;
        }

        throw new IllegalArgumentException("Unknown identifier: " + identifier);
    }

    private String parseString() {
        final char quote = this.consume();
        final StringBuilder sb = new StringBuilder();

        while (this.pos < this.chars.length && this.peek() != quote) {
            if (this.peek() == '\\') {
                this.consume();
                if (this.pos < this.chars.length) {
                    final char escaped = this.consume();
                    switch (escaped) {
                        case 'n' -> sb.append('\n');
                        case 't' -> sb.append('\t');
                        case 'r' -> sb.append('\r');
                        case '\\' -> sb.append('\\');
                        case '\'' -> sb.append('\'');
                        case '"' -> sb.append('"');
                        default -> sb.append(escaped);
                    }
                }
            } else {
                sb.append(this.consume());
            }
        }

        this.expect(String.valueOf(quote));
        return sb.toString();
    }

    private Number parseNumber() {
        final int start = this.pos;

        if (this.peek() == '-') {
            this.consume();
        }

        boolean hasDecimal = false;
        while (this.pos < chars.length) {
            final char c = this.peek();
            if (Character.isDigit(c)) {
                this.consume();
            } else if (c == '.' && !hasDecimal) {
                hasDecimal = true;
                this.consume();
            } else {
                break;
            }
        }

        final String numStr = this.rawCondition.substring(start, this.pos);

        if (numStr.equals("-") || numStr.isEmpty()) {
            throw new IllegalArgumentException("Invalid number at position " + start);
        }

        if (hasDecimal) {
            return Double.parseDouble(numStr);
        } else {
            try {
                return Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                return Long.parseLong(numStr);
            }
        }
    }

    private String parseIdentifier() {
        final int start = this.pos;

        while (this.pos < this.chars.length) {
            final char c = this.peek();
            if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                this.consume();
            } else {
                break;
            }
        }

        if (start == this.pos) {
            throw new IllegalArgumentException("Expected identifier at position " + this.pos);
        }

        return this.rawCondition.substring(start, this.pos);
    }

    private void skipWhitespace() {
        while (this.pos < this.chars.length && Character.isWhitespace(this.chars[this.pos])) {
            this.pos++;
        }
    }

    private boolean match(String str) {
        final char[] charArray = str.toCharArray();
        final int savePos = this.pos;

        for (int i = 0; i < charArray.length; i++) {
            if (this.pos >= this.chars.length || this.chars[this.pos] != charArray[i]) {
                this.pos = savePos;
                return false;
            }
            this.pos++;
        }

        return true;
    }

    private void expect(String str) {
        if (!this.match(str)) {
            throw new IllegalArgumentException("Expected '" + str + "' at position " + this.pos);
        }
    }

    private char peek() {
        if (this.pos >= this.chars.length)
            throw new IllegalArgumentException("Unexpected end of input at position " + this.pos);
        return this.chars[this.pos];
    }

    private char consume() {
        return this.chars[this.pos++];
    }

    private String collectRest() {
        final StringBuilder restBuilder = new StringBuilder();
        for (int i = this.pos; i < this.chars.length; i++) {
            restBuilder.append(this.chars[i]);
        }
        return restBuilder.toString();
    }
}
