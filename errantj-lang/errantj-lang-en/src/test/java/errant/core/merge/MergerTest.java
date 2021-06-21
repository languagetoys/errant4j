package errant.core.merge;

import edu.guym.errantj.lang.en.aligner.AlignerSupplier;
import edu.guym.errantj.lang.en.lemmatize.Lemmatizer;
import edu.guym.errantj.lang.en.lemmatize.WordNetLemmatizer;
import edu.guym.errantj.lang.en.merge.ErrantMerger;
import edu.guym.spacyj.api.containers.Doc;
import edu.guym.spacyj.api.containers.Token;
import io.squarebunny.aligner.Aligner;
import io.squarebunny.aligner.alignment.Alignment;
import io.squarebunny.aligner.edit.Edit;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static errant.TestTools.parse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MergerTest {

    private final Lemmatizer lemmatizer = new WordNetLemmatizer();

    @Test
    public void testMergeInfinitivalSamePos() {
        Doc source = parse("I like to eat food.");
        Doc target = parse("I like eating food.");
        Aligner<Token> aligner = AlignerSupplier.create(lemmatizer).get();
        Alignment<Token> alignment = aligner.align(source.tokens(), target.tokens());
        ErrantMerger merger = new ErrantMerger();
        List<Edit<String>> merged = merger.merge(alignment.edits())
                .stream()
                .map(edit -> edit.map(Token::text))
                .collect(Collectors.toList());
        assertTrue(merged.contains(Edit.builder().substitute("to", "eat").with("eating").atPosition(2, 2)));
    }

    @Test
    public void testMergeInfinitivalSamePos2() {
        Doc source = parse("I eated dinner yesterday");
        Doc target = parse("I have eaten dinner yesterday");
        Aligner<Token> aligner = AlignerSupplier.create(lemmatizer).get();
        Alignment<Token> alignment = aligner.align(source.tokens(), target.tokens());
        ErrantMerger merger = new ErrantMerger();
        List<Edit<String>> merged = merger.merge(alignment.edits())
                .stream()
                .map(edit -> edit.map(Token::text))
                .collect(Collectors.toList());
        assertTrue(merged.contains(Edit.builder().substitute("eated").with("have", "eaten").atPosition(1, 1)));
    }
}