class UseOptionalOrElseGetVsOrElse {

    public static final String name = Optional.of("ecoCode").orElse(getUnpredictedMethod()); // Noncompliant {{Use optional orElseGet instead of orElse.}}

    public static final String name = Optional.of("ecoCode").orElseGet(getUnpredictedMethod()); // Compliant

    public static final String name = randomClass.orElse(); // Compliant
}
