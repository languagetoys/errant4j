package io.languagetoys.errant4j.lang.en.classify.rules;

import io.languagetoys.aligner.edit.Edit;
import io.languagetoys.errant4j.core.classify.Classifier;
import io.languagetoys.errant4j.core.grammar.GrammaticalError;
import io.languagetoys.errant4j.core.tools.Collectors;
import io.languagetoys.errant4j.lang.en.classify.rules.common.Predicates;
import io.languagetoys.spacy4j.api.containers.Token;
import io.languagetoys.spacy4j.api.features.Pos;
import io.languagetoys.spacy4j.api.features.Tag;

import java.util.function.Predicate;

/**
 * Since it is fairly common for the POS tagger to confuse nouns that look like adjectives, e.g. musical,
 * a separate rule uses fine-grained POS tags to matchError mis-tagged edits such as [musical (ADJ) → musicals (NOUN)]:
 * 1. There is exactly one token on both sides of the edit, and
 * 2. Both tokens have the same lemma, and
 * 3. The original token is POS tagged as ADJ, and
 * 4. The corrected token is POS tagged as a plural noun (NNS).
 * Note that this second rule was only found to be effective in the singular to plural direction and not the other way around.
 */
public class NounNumberAdjConfusion extends Classifier.Predicate {

    @Override
    public GrammaticalError.Category getCategory() {
        return GrammaticalError.Category.NOUN_NUM;
    }

    @Override
    public boolean test(Edit<Token> edit) {
        return edit
                .filter(Predicates.isSubstitute())
                .filter(Predicates.ofSizeOneToOne())
                .filter(sameLemma())
                .filter(e -> e.source().map(Token::pos).allMatch(Pos.ADJ::matches))
                .filter(e -> e.target().map(Token::tag).allMatch(Tag.NNS::matches))
                .isPresent();
    }

    public Predicate<Edit<Token>> sameLemma() {
        return edit -> edit.stream().map(Token::lemma).collect(Collectors.oneOrNone()).isPresent();
    }
}
