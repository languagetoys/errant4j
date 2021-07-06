package io.languagetoys.errant4j.core.annotator;

import io.languagetoys.aligner.edit.Edit;
import io.languagetoys.errant4j.core.grammar.GrammaticalError;
import io.languagetoys.errant4j.core.tools.TokenEditUtils;
import io.languagetoys.errant4j.core.tools.mark.CharOffset;
import io.languagetoys.errant4j.core.tools.mark.ErrorMarker;
import io.languagetoys.spacy4j.api.containers.Token;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An Annotation matches an {@link Edit} with its associated {@link GrammaticalError}.
 *
 */
public final class Annotation {

    private final Edit<Token> edit;
    private final GrammaticalError error;

    private Annotation(Edit<Token> edit) {
        this(edit, GrammaticalError.NONE);
    }

    public Annotation(Edit<Token> edit, GrammaticalError error) {
        this.edit = edit;
        this.error = error;
    }

    public static Annotation create(Edit<Token> edit, GrammaticalError error) {
        return new Annotation(edit, error);
    }

    public static Annotation of(Edit<Token> edit) {
        return new Annotation(edit);
    }

    public final Edit<Token> edit() {
        return edit;
    }

    public final String sourceText() {
        return TokenEditUtils.getSourceText(edit);
    }

    public final String targetText() {
        return TokenEditUtils.getTargetText(edit);
    }

    public final boolean matches(Predicate<? super Annotation> predicate) {
        return predicate.test(this);
    }

    public final <R> R transform(Function<? super Annotation, ? extends R> mapper) {
        return mapper.apply(this);
    }

    public final Annotation withError(GrammaticalError error) {
        return new Annotation(edit(), error);
    }

    public final GrammaticalError grammaticalError() {
        return error;
    }

    public final boolean hasError() {
        return !grammaticalError().isNone() && !grammaticalError().isIgnored();
    }

    public final boolean hasNoError() {
        return !hasError();
    }

    public final CharOffset markErrorInSource() {
        return edit.accept(new ErrorMarker(edit.source().tokens()));
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Annotation that = (Annotation) o;
        return error == that.error;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(error);
    }

    @Override
    public final String toString() {
        return "Annotation{" +
                "edit=" + edit +
                "error=" + error +
                "} ";
    }
}