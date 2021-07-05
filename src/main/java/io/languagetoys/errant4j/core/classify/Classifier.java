package io.languagetoys.errant4j.core.classify;

import io.languagetoys.aligner.edit.Edit;
import io.languagetoys.errant4j.core.grammar.GrammaticalError;
import io.languagetoys.spacy4j.api.containers.Token;

import java.util.List;


public interface Classifier {

    /**
     * Get the {@link GrammaticalError} for the supplied Edit.
     */
    GrammaticalError classify(Edit<Token> edit);

    static Classifier rules(List<Rule> rules) {
        return new RuleBasedClassifier(rules);
    }

    interface Rule {
        GrammaticalError classify(Edit<Token> edit);
    }

    abstract class Predicate implements Rule, java.util.function.Predicate<Edit<Token>> {

        @Override
        public GrammaticalError classify(Edit<Token> edit) {
            return test(edit) ? GrammaticalError.of(edit, getCategory()) : GrammaticalError.unknown(edit);
        }

        public abstract GrammaticalError.Category getCategory();

        public abstract boolean test(Edit<Token> edit);
    }
}
