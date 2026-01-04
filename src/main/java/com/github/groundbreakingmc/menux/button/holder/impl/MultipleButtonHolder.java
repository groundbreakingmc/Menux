package com.github.groundbreakingmc.menux.button.holder.impl;

import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class MultipleButtonHolder implements ButtonHolder {

    private final ButtonTemplate[] templates;

    public MultipleButtonHolder(List<ButtonTemplate> templates) {
        this(templates.toArray(new ButtonTemplate[0]));
    }

    public MultipleButtonHolder(ButtonTemplate[] templates) {
        Arrays.sort(
                templates,
                Comparator.comparingInt(ButtonTemplate::renderPriority).reversed()
        );
        this.templates = templates;
    }

    @Override
    public @Nullable ButtonTemplate button(@Nullable MenuContext context) {
        for (final ButtonTemplate template : this.templates) {
            final List<MenuRule> viewRequirements = template.viewRequirements();
            if (this.passAll(context, viewRequirements)) return template;
        }
        return null;
    }

    @Override
    public @NotNull List<ButtonTemplate> all() {
        return List.of(this.templates);
    }

    private boolean passAll(MenuContext context, List<MenuRule> requirements) {
        if (context == null) return requirements.isEmpty();
        for (int i = 0; i < requirements.size(); i++) {
            final MenuRule rule = requirements.get(i);
            if (!rule.test(context)) return false;
        }
        return true;
    }
}
