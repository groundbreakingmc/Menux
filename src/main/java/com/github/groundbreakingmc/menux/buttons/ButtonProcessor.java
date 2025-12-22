package com.github.groundbreakingmc.menux.buttons;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ButtonProcessor(@NotNull ButtonTemplate template) {

    public void processClick(@NotNull MenuContext context, @NotNull ClickType clickType) {
        final ClickParams clickParams = this.template.clickActions().get(clickType);
        if (clickParams != null) {
            List<MenuAction> actions = clickParams.actions();

            final List<MenuRule> conditions = clickParams.conditions();
            for (int i = 0, size = conditions.size(); i < size; i++) {
                final MenuRule rule = conditions.get(i);
                if (!rule.test(context)) {
                    actions = rule.denyActions();
                }
            }

            for (int i = 0, size = actions.size(); i < size; i++) {
                actions.get(i).run(context);
            }
        }
    }
}
