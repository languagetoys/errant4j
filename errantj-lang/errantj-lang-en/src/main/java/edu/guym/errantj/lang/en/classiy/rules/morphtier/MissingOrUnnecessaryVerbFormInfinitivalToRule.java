package edu.guym.errantj.lang.en.classiy.rules.morphtier;

import edu.guym.errantj.core.classify.Category;
import edu.guym.errantj.core.classify.GrammaticalError;
import edu.guym.errantj.core.classify.rules.Rule;
import edu.guym.spacyj.api.containers.Token;
import edu.guym.spacyj.api.features.PtbPos;
import edu.guym.spacyj.api.features.UdPos;
import io.squarebunny.aligner.edit.Edit;
import io.squarebunny.aligner.edit.predicates.EditPredicates;

import java.util.function.Predicate;

/**
 * Verb form errors involve corrections between members of the set of bare infinitive, to-
 * infinitive, gerund and participle forms; e.g. {eat, to eat, eating, eaten}.
 * Since infinitives tend to have exactly the same form as non-3rd-person present tense forms however
 * (cf. ‘I want to eat cake’ versus ‘I eat cake’), we must use fine-grained POS tags to differentiate between them.
 * <p>
 * Other types of verb form errors involve infinitival to.
 * The next rule hence captures missing or unnecessary to particles that are not prepositions:
 * <p>
 * 1. There is only one token on one side of the edit, and
 * 2. That token is to, and
 * 3. That token is POS tagged as PART, and
 * 4. That token is not parsed as prep.
 */
public class MissingOrUnnecessaryVerbFormInfinitivalToRule implements Rule {

    @Override
    public GrammaticalError apply(Edit<Token> edit) {
        return edit
                .filter(onlyOneTokenOnOneSide())
                .filter(tokenIsTo())
                .filter(tokenIsPART())
                .filter(tokenIsNotPrep())
                .map(classify(Category.VERB_FORM))
                .orElse(unknown(edit));
    }

    public Predicate<Edit<Token>> onlyOneTokenOnOneSide() {
        return edit -> edit.matches(EditPredicates.ofSize(1, 0)) || edit.matches(EditPredicates.ofSize(0, 1));
    }

    public Predicate<Edit<Token>> tokenIsTo() {
        return edit -> edit
                .stream()
                .findFirst()
                .filter(token -> token.lowerCase().equals("to"))
                .isPresent();
    }

    public Predicate<Edit<Token>> tokenIsPART() {
        return edit -> edit
                .stream()
                .findFirst()
                .filter(token -> UdPos.PART.matches(token.pos()))
                .isPresent();
    }

    public Predicate<Edit<Token>> tokenIsNotPrep() {
        return edit -> edit
                .stream()
                .findFirst()
                .filter(token -> PtbPos.IN.matches(token.tag()))
                .isEmpty();
    }

}