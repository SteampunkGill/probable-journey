package com.milktea.milktea_backend.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Cleanup {
    public static void main(String[] args) throws IOException {
        String entityDir = "src/main/java/com/milktea/milktea_backend/model/entity";
        
        // 要保留的单数类（手动创建的）
        List<String> keep = Arrays.asList(
            "MemberLevel.java",
            "User.java",
            "UserAddress.java",
            "UserTag.java",
            "UserTagRelation.java",
            "Category.java",
            "Product.java",
            "ProductNutrition.java",
            "Ingredient.java"
        );
        
        // 要删除的复数类
        List<String> delete = Arrays.asList(
            "MemberLevels.java",
            "Users.java",
            "UserAddresses.java",
            "UserTags.java",
            "Categories.java",
            "Products.java",
            "ProductNutritions.java",
            "Ingredients.java"
        );
        
        for (String fileName : delete) {
            Path filePath = Paths.get(entityDir, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Deleted " + fileName);
            }
        }
    }
}