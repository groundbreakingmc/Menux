package com.github.groundbreakingmc.menux.button.holder.impl;

import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class MultipleButtonHolder implements ButtonHolder {

    private final ButtonTemplate[] templates;

    public MultipleButtonHolder(List<ButtonTemplate> templates) {
        this(templates.toArray(new ButtonTemplate[0]));
    }

    public MultipleButtonHolder(ButtonTemplate[] templates) {
        this.templates = templates;
    }

    @Override
    public @Nullable ButtonTemplate button(@Nullable MenuContext context) {
        for (int i = this.templates.length - 1; i >= 0; i--) {
            final ButtonTemplate template = this.templates[i];
            final List<MenuRule> viewRequirements = template.viewRequirements();
            if (this.passAll(context, viewRequirements)) return template;
        }
        return null;
    }

    private boolean passAll(MenuContext context, List<MenuRule> requirements) {
        if (context == null) return requirements.isEmpty();
        for (int j = 0; j < requirements.size(); j++) {
            final MenuRule rule = requirements.get(j);
            if (!rule.test(context)) return false;
        }
        return true;
    }
}
