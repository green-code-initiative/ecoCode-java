package fr.greencodeinitiative.java.checks;

class AvoidMultipleIfElseStatementCheck {

    public int compare(FieldVo o1, FieldVo o2) {

        if (o1.getIdBlock().equals(o2.getIdBlock())) {
            if (o1.getIdField().equals(o2.getIdField())) {
                return 0;
            }
            // First original
            if (o1.isOriginal() && !o2.isOriginal()) {
                return -1;
            } else if (!o1.isOriginal() && o2.isOriginal()) {
                return 1;
            }
            // First min posgafld
            Long result = o1.getColumnPos() - o2.getColumnPos();
            if (result != 0) {
                return result.intValue();
            }

            // First min ordgaflc
            result = o1.getIndex() - o2.getIndex();
            return result.intValue();
        }
        // First BQRY block
        if (o1.getIdBlock().startsWith("BQRY") && !o2.getIdBlock().startsWith("BQRY")) {
            return -1;
        } else if (!o1.getIdBlock().startsWith("BQRY") && o2.getIdBlock().startsWith("BQRY")) {
            return 1;
        }
        // If both block don't start with BQRY, sort alpha with String.compareTo method
        return o1.getIdBlock().compareTo(o2.getIdBlock());
    }

}
