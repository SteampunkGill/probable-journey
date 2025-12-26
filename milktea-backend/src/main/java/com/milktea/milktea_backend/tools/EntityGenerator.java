package com.milktea.milktea_backend.tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityGenerator {
    
    public static void main(String[] args) throws Exception {
        String sqlFile = "src/main/resources/db/migration/initial.sql";
        String outputDir = "src/main/java/com/milktea/milktea_backend/model/entity";
        
        List<Table> tables = parseSqlFile(sqlFile);
        generateEntities(tables, outputDir);
        
        System.out.println("Generated " + tables.size() + " entity classes.");
    }
    
    static class Table {
        String name;
        String comment;
        List<Column> columns = new ArrayList<>();
        List<ForeignKey> foreignKeys = new ArrayList<>();
    }
    
    static class Column {
        String name;
        String type;
        boolean nullable;
        boolean primaryKey;
        String comment;
        String defaultValue;
    }
    
    static class ForeignKey {
        String columnName;
        String referencedTable;
        String referencedColumn;
    }
    
    private static List<Table> parseSqlFile(String sqlFile) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(sqlFile)));
        List<Table> tables = new ArrayList<>();
        
        // 简化解析：查找 CREATE TABLE 语句
        Pattern tablePattern = Pattern.compile("CREATE TABLE `(\\w+)`\\s*\\(([\\s\\S]*?)\\)\\s*ENGINE", Pattern.CASE_INSENSITIVE);
        Matcher tableMatcher = tablePattern.matcher(content);
        
        while (tableMatcher.find()) {
            Table table = new Table();
            table.name = tableMatcher.group(1);
            String columnsText = tableMatcher.group(2);
            
            // 解析列
            List<Column> columns = new ArrayList<>();
            String[] lines = columnsText.split("\\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("PRIMARY KEY") || 
                    line.startsWith("UNIQUE KEY") || line.startsWith("KEY") || line.startsWith("CONSTRAINT")) {
                    // 跳过约束和索引
                    continue;
                }
                
                // 匹配列定义: `column_name` TYPE ...
                Pattern colPattern = Pattern.compile("`(\\w+)`\\s+(\\w+)(?:\\((\\d+(?:,\\d+)?)\\))?\\s*(.*)");
                Matcher colMatcher = colPattern.matcher(line);
                if (colMatcher.find()) {
                    Column col = new Column();
                    col.name = colMatcher.group(1);
                    col.type = colMatcher.group(2).toUpperCase();
                    // 简化处理
                    col.nullable = !line.contains("NOT NULL");
                    col.primaryKey = line.contains("PRIMARY KEY") || line.contains("AUTO_INCREMENT");
                    columns.add(col);
                }
            }
            
            table.columns = columns;
            tables.add(table);
        }
        
        return tables;
    }
    
    private static void generateEntities(List<Table> tables, String outputDir) throws IOException {
        for (Table table : tables) {
            String className = toCamelCase(table.name, true);
            String fileName = className + ".java";
            Path filePath = Paths.get(outputDir, fileName);
            
            // 如果文件已存在，跳过（避免覆盖手动编写的实体）
            if (Files.exists(filePath)) {
                System.out.println("Skipping " + fileName + " (already exists)");
                continue;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("package com.milktea.milktea_backend.model.entity;\n\n");
            sb.append("import jakarta.persistence.*;\n");
            sb.append("import lombok.*;\n");
            sb.append("import org.hibernate.annotations.CreationTimestamp;\n");
            sb.append("import org.hibernate.annotations.UpdateTimestamp;\n\n");
            sb.append("import java.math.BigDecimal;\n");
            sb.append("import java.time.LocalDate;\n");
            sb.append("import java.time.LocalDateTime;\n\n");
            sb.append("@Entity\n");
            sb.append("@Table(name = \"").append(table.name).append("\")\n");
            sb.append("@Getter\n");
            sb.append("@Setter\n");
            sb.append("@NoArgsConstructor\n");
            sb.append("@AllArgsConstructor\n");
            sb.append("@ToString\n");
            sb.append("@EqualsAndHashCode\n");
            sb.append("public class ").append(className).append(" {\n\n");
            
            // 生成字段
            for (Column col : table.columns) {
                String fieldName = toCamelCase(col.name, false);
                String javaType = mapSqlTypeToJava(col.type);
                
                sb.append("    @Column(name = \"").append(col.name).append("\"");
                if (!col.nullable && !col.primaryKey) {
                    sb.append(", nullable = false");
                }
                // 添加其他属性
                sb.append(")\n");
                sb.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n\n");
            }
            
            sb.append("}\n");
            
            Files.write(filePath, sb.toString().getBytes());
            System.out.println("Generated " + fileName);
        }
    }
    
    private static String toCamelCase(String snakeCase, boolean firstLetterUpper) {
        String[] parts = snakeCase.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == 0 && !firstLetterUpper) {
                result.append(part.toLowerCase());
            } else {
                result.append(Character.toUpperCase(part.charAt(0)));
                result.append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    
    private static String mapSqlTypeToJava(String sqlType) {
        switch (sqlType.toUpperCase()) {
            case "BIGINT":
                return "Long";
            case "INT":
            case "TINYINT":
            case "SMALLINT":
            case "MEDIUMINT":
                return "Integer";
            case "DECIMAL":
            case "NUMERIC":
                return "BigDecimal";
            case "VARCHAR":
            case "CHAR":
            case "TEXT":
            case "LONGTEXT":
            case "MEDIUMTEXT":
                return "String";
            case "DATE":
                return "LocalDate";
            case "DATETIME":
            case "TIMESTAMP":
                return "LocalDateTime";
            case "BOOLEAN":
            case "BIT":
                return "Boolean";
            case "JSON":
                return "String";
            default:
                return "String";
        }
    }
}