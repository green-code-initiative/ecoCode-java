package fr.greencodeinitiative.java.checks;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

import java.util.Arrays;
import java.util.List;

@Rule(key = "EC501")
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "S501")
public class SVGImageCheck extends IssuableSubscriptionVisitor {

    private static final String MESSAGE_RULES = "Consider using SVG format over other image formats for small images.";
    private static final List<String> IMAGE_FORMATS = Arrays.asList("jpeg", "jpg", "png");
    private static final int MAX_ACCEPTABLE_SIZE = 100000;

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return List.of(Tree.Kind.STRING_LITERAL);
    }

    @Override
    public void visitNode(Tree tree) {
        String imagePath = ((LiteralTree) tree).value().replaceAll("\"", "");
        if (isImage(imagePath)) {
            String fileExtension = getFileExtension(imagePath);
            if (!fileExtension.equalsIgnoreCase("svg")) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    int imageSize = getImageSize(imageFile);
                    if (imageSize > 0 && imageSize < MAX_ACCEPTABLE_SIZE) {
                        reportIssue(tree, MESSAGE_RULES);
                    }
                }
            }
        }
    }

    private boolean isImage(String filename) {
        String fileExtension = getFileExtension(filename);
        return IMAGE_FORMATS.contains(fileExtension.toLowerCase());
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    private int getImageSize(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                return image.getWidth() * image.getHeight();
            }
        } catch (Exception e) {
           System.err.println("Image is not provided");
        }
        return 0;
    }

}
