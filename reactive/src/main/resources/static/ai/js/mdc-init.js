document.addEventListener("DOMContentLoaded", () => {
  // MDC komponensek inicializálása
  window.textFields = [].map.call(
    document.querySelectorAll(".mdc-text-field"),
    (el) => new mdc.textField.MDCTextField(el),
  );
  window.buttons = [].map.call(
    document.querySelectorAll(".mdc-button"),
    (el) => new mdc.ripple.MDCRipple(el),
  );
  window.dictateSelect = new mdc.select.MDCSelect(
    document.querySelector("#dictate-select"),
  );
  window.voiceSelectMdc = new mdc.select.MDCSelect(
    document.querySelector("#voice-select"),
  );
  window.rateSlider = new mdc.slider.MDCSlider(
    document.querySelector("#rate-slider"),
  );
  window.pitchSlider = new mdc.slider.MDCSlider(
    document.querySelector("#pitch-slider"),
  );
});
