package edu.guym.errantj.lang.en.classiy.rules.morphtier;

import edu.guym.errantj.core.classify.Category;
import edu.guym.errantj.core.classify.GrammaticalError;
import edu.guym.errantj.lang.en.classiy.common.TokenPredicates;
import edu.guym.errantj.core.classify.rules.Rule;
import edu.guym.errantj.wordlist.WordList;
import io.squarebunny.aligner.edit.Edit;
import io.squarebunny.aligner.edit.predicates.EditPredicates;
import edu.guym.spacyj.api.containers.Token;

import java.util.function.Predicate;

/**
 * Verb inflection errors are classified in a similar manner to noun inflection errors,
 * and are a special kind of non-word error.
 * Examples include: [getted → got],[danceing → dancing] and [fliped → flipped].
 * 1. There is exactly one token on both sides of the edit, and
 * 2. The original token is entirely alphabetical (i.e. no numbers or punctuation), and
 * 3. The original token is not in the Hunspell word list, and
 * 4. The lower cased form of the original token is also not in the Hunspell word list, and
 * 5. The original and corrected tokens have the same lemma, and
 * 6. The original and corrected tokens are both POS tagged as VERB.
 */
public class VerbInflectionRule implements Rule {

    private final WordList wordList;

    public VerbInflectionRule(WordList wordList) {
        this.wordList = wordList;
    }

    @Override
    public GrammaticalError apply(Edit<Token> edit) {
        return edit
                .filter(EditPredicates.ofSizeOneToOne())
                .filter(e -> e.source().allMatch(Token::isAlpha))
                .filter(e -> e.source().allMatch(isNotRealWord()))
                .filter(sameLemma())
                .filter(e -> e.stream().allMatch(TokenPredicates.isVerb()))
                .map(classify(Category.VERB_INFL))
                .orElse(unknown(edit));
    }

    public Predicate<Token> isNotRealWord() {
        return token -> !wordList.contains(token.text()) && !wordList.contains(token.lowerCase());
    }


    public Predicate<Edit<Token>> sameLemma() {
        return edit -> edit.source().first().lemma().equals(edit.target().first().lemma());
    }
}