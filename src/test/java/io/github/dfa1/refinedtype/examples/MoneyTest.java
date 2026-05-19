package io.github.dfa1.refinedtype.examples;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class MoneyTest {

    private static final CurrencyCode USD = CurrencyCode.of("USD");
    private static final CurrencyCode EUR = CurrencyCode.of("EUR");

    // ── construction ─────────────────────────────────────────────────────────

    @Test
    void constructFromPriceAndCurrency() {
        // Given / When
        var sut = Money.of(Price.of(100.0), USD);

        // Then
        assertThat(sut.amount().value()).isEqualTo(100.0);
        assertThat(sut.currency()).isEqualTo(USD);
    }

    @Test
    void constructFromDoubleAndCurrency() {
        // Given / When
        var sut = Money.of(99.99, USD);

        // Then
        assertThat(sut.amount().value()).isEqualTo(99.99);
        assertThat(sut.currency()).isEqualTo(USD);
    }

    @Test
    void negativeAmountAllowed() {
        // Given / When — overdraft, negative P&L
        var sut = Money.of(-50.0, USD);

        // Then
        assertThat(sut.amount().value()).isEqualTo(-50.0);
    }

    @Test
    void nullAmountRejected() {
        assertThatThrownBy(() -> Money.of(null, USD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullCurrencyRejected() {
        assertThatThrownBy(() -> Money.of(Price.of(1.0), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── add ──────────────────────────────────────────────────────────────────

    @Test
    void addSameCurrency() {
        // Given
        var sut = Money.of(100.0, USD);
        var other = Money.of(50.0, USD);

        // When
        Money result = sut.add(other);

        // Then
        assertThat(result.amount().value()).isCloseTo(150.0, within(1e-9));
        assertThat(result.currency()).isEqualTo(USD);
    }

    @Test
    void addDifferentCurrencyRejected() {
        // Given
        var sut = Money.of(100.0, USD);
        var other = Money.of(100.0, EUR);

        // When / Then
        assertThatThrownBy(() -> sut.add(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("USD")
                .hasMessageContaining("EUR");
    }

    // ── subtract ─────────────────────────────────────────────────────────────

    @Test
    void subtractSameCurrency() {
        // Given
        var sut = Money.of(100.0, USD);
        var other = Money.of(30.0, USD);

        // When
        Money result = sut.subtract(other);

        // Then
        assertThat(result.amount().value()).isCloseTo(70.0, within(1e-9));
        assertThat(result.currency()).isEqualTo(USD);
    }

    @Test
    void subtractProducesNegative() {
        // Given
        var sut = Money.of(10.0, USD);
        var other = Money.of(20.0, USD);

        // When
        Money result = sut.subtract(other);

        // Then
        assertThat(result.amount().value()).isCloseTo(-10.0, within(1e-9));
    }

    @Test
    void subtractDifferentCurrencyRejected() {
        // Given
        var sut = Money.of(100.0, USD);
        var other = Money.of(100.0, EUR);

        // When / Then
        assertThatThrownBy(() -> sut.subtract(other))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── multiply ─────────────────────────────────────────────────────────────

    @Test
    void multiplyByPositiveFactor() {
        // Given
        var sut = Money.of(50.0, USD);

        // When
        Money result = sut.multiply(3.0);

        // Then
        assertThat(result.amount().value()).isCloseTo(150.0, within(1e-9));
        assertThat(result.currency()).isEqualTo(USD);
    }

    @Test
    void multiplyByZeroGivesZero() {
        // Given
        var sut = Money.of(100.0, USD);

        // When
        Money result = sut.multiply(0.0);

        // Then
        assertThat(result.isZero()).isTrue();
    }

    @Test
    void multiplyByNegativeFactorNegatesAmount() {
        // Given
        var sut = Money.of(100.0, USD);

        // When
        Money result = sut.multiply(-1.0);

        // Then
        assertThat(result.amount().value()).isCloseTo(-100.0, within(1e-9));
    }

    // ── negate ───────────────────────────────────────────────────────────────

    @Test
    void negateFlipsSign() {
        // Given
        var sut = Money.of(100.0, USD);

        // When
        Money result = sut.negate();

        // Then
        assertThat(result.amount().value()).isCloseTo(-100.0, within(1e-9));
        assertThat(result.currency()).isEqualTo(USD);
    }

    @Test
    void negateNegativeGivesPositive() {
        // Given
        var sut = Money.of(-40.0, USD);

        // When
        Money result = sut.negate();

        // Then
        assertThat(result.amount().value()).isCloseTo(40.0, within(1e-9));
    }

    // ── sign predicates ───────────────────────────────────────────────────────

    @Test
    void isPositiveTrue() {
        assertThat(Money.of(1.0, USD).isPositive()).isTrue();
    }

    @Test
    void isPositiveFalseForNegative() {
        assertThat(Money.of(-1.0, USD).isPositive()).isFalse();
    }

    @Test
    void isNegativeTrue() {
        assertThat(Money.of(-1.0, USD).isNegative()).isTrue();
    }

    @Test
    void isNegativeFalseForPositive() {
        assertThat(Money.of(1.0, USD).isNegative()).isFalse();
    }

    @Test
    void isZeroTrue() {
        assertThat(Money.of(0.0, USD).isZero()).isTrue();
    }

    @Test
    void isZeroFalseForNonZero() {
        assertThat(Money.of(0.01, USD).isZero()).isFalse();
    }

    // ── equality ─────────────────────────────────────────────────────────────

    @Test
    void equalsSameAmountAndCurrency() {
        // Given
        var sut   = Money.of(100.0, USD);
        var other = Money.of(100.0, USD);

        // Then
        assertThat(sut).isEqualTo(other);
    }

    @Test
    void equalsDifferentAmount() {
        assertThat(Money.of(100.0, USD)).isNotEqualTo(Money.of(200.0, USD));
    }

    @Test
    void equalsDifferentCurrency() {
        assertThat(Money.of(100.0, USD)).isNotEqualTo(Money.of(100.0, EUR));
    }

    @Test
    void hashCodeConsistentForEqualValues() {
        // Given
        var sut   = Money.of(100.0, USD);
        var other = Money.of(100.0, USD);

        // Then
        assertThat(sut.hashCode()).isEqualTo(other.hashCode());
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = Money.of(99.99, USD);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Money(99.99 USD)");
    }
}
