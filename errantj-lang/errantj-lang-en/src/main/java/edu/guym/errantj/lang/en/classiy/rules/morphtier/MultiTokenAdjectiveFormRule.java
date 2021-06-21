package edu.guym.errantj.lang.en.classiy.rules.morphtier;

import edu.guym.errantj.core.classify.Category;
import edu.guym.errantj.core.classify.GrammaticalError;
import edu.guym.errantj.core.classify.rules.Rule;
import edu.guym.errantj.lang.en.lemmatize.Lemmatizer;
import io.squarebunny.aligner.edit.Edit;
import io.squarebunny.aligner.edit.Segment;
import edu.guym.spacyj.api.containers.Token;
import io.squarebunny.aligner.edit.predicates.EditPredicates;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Adjective form edits involve changes between bare, comparative and superlative adjective forms;
 * <p>
 * A second rule captures multi-token adjective form errors; e.g. [more big → bigger]:
 * 1. There are no more than two tokens on both sides of the edit, and
 * 2. The first token on either side is more or most, and
 * 3. The last token on both sides has the same lemma.
 */
public class MultiTokenAdjectiveFormRule implements Rule {

    private final Lemmatizer lemmatizer;

    public MultiTokenAdjectiveFormRule(Lemmatizer lemmatizer) {
        this.lemmatizer = lemmatizer;
    }

    @Override
    public GrammaticalError apply(Edit<Token> edit) {
        return edit
                .filter(EditPredicates.isSubstitute())
                .filter(EditPredicates.ofMaxSize(2, 2))
                .filter(firstTokensAnyMatch(moreOrMost()))
                .filter(lastTokensHasSameLemma())
                .map(classify(Category.ADJ_FORM))
                .orElse(unknown(edit));
    }

    public Predicate<? super Token> moreOrMost() {
        return word -> Set.of("more", "most").contains(word.lowerCase());
    }

    public Predicate<? super Edit<Token>> firstTokensAnyMatch(Predicate<? super Token> condition) {
        return edit -> edit
                .streamSegments(Segment::first, Segment::first)
                .anyMatch(condition);
    }

    public Predicate<? super Edit<Token>> lastTokensHasSameLemma() {
        return edit -> {
            Set<String> sourceLemmas = lemmatizer.lemmas(edit.source().last().text());
            Set<String> targetLemmas = lemmatizer.lemmas(edit.target().last().text());
            return !Collections.disjoint(sourceLemmas, targetLemmas);
        };
    }


}