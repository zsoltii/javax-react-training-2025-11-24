document.addEventListener("DOMContentLoaded", () => {
  let utterance; // Globális változó a garbage collection elkerülésére
  let speechPingInterval; // Intervallum a beszéd fenntartásához

  window.stopSpeech = () => {
    if ("speechSynthesis" in window) {
      window.speechSynthesis.cancel();
    }
    if (speechPingInterval) {
      clearInterval(speechPingInterval);
      speechPingInterval = null;
    }
  };

  const readButton = document.getElementById("readButton");
  const stopButton = document.getElementById("stopButton");
  const rateValue = document.getElementById("rateValue");
  const pitchValue = document.getElementById("pitchValue");

  let voices = [];

  function populateVoiceList() {
    voices = window.speechSynthesis.getVoices();
    const voiceList = document.getElementById("voiceSelect");
    voiceList.innerHTML = "";
    voices.forEach((voice, i) => {
      const li = document.createElement("li");
      li.className = "mdc-list-item";
      li.setAttribute("data-value", voice.name);
      let content = `${voice.name} (${voice.lang})`;
      if (voice.default) {
        content += " -- DEFAULT";
      }
      li.innerHTML = `<span class="mdc-list-item__ripple"></span><span class="mdc-list-item__text">${content}</span>`;
      voiceList.appendChild(li);
    });
    // Frissítjük az MDC komponenst, hogy tudjon az új elemekről
    voiceSelectMdc.layoutOptions();
  }

  populateVoiceList();
  if (speechSynthesis.onvoiceschanged !== undefined) {
    speechSynthesis.onvoiceschanged = populateVoiceList;
  }

  readButton.addEventListener("click", () => {
    if (window.fullResponseContent && "speechSynthesis" in window) {
      window.stopSpeech(); // Leállítjuk az előző beszédet és a pinget
      utterance = new SpeechSynthesisUtterance(window.fullResponseContent);
      const selectedVoiceName = voiceSelectMdc.value;
      const selectedVoice = voices.find(
        (voice) => voice.name === selectedVoiceName,
      );
      utterance.voice = selectedVoice;
      utterance.pitch = pitchSlider.getValue();
      utterance.rate = rateSlider.getValue();

      utterance.onend = () => {
        window.stopSpeech();
      };

      window.speechSynthesis.speak(utterance);

      // "Ping" mechanizmus a beszéd életben tartására
      speechPingInterval = setInterval(() => {
        if (window.speechSynthesis.speaking) {
          window.speechSynthesis.pause();
          window.speechSynthesis.resume();
        } else {
          window.stopSpeech();
        }
      }, 10000); // 10 másodpercenként
    }
  });

  stopButton.addEventListener("click", window.stopSpeech);

  rateSlider.listen("MDCSlider:input", () => {
    rateValue.textContent = rateSlider.getValue().toFixed(1);
  });

  pitchSlider.listen("MDCSlider:input", () => {
    pitchValue.textContent = pitchSlider.getValue().toFixed(1);
  });

  window.addEventListener("beforeunload", window.stopSpeech);

  const dictateButton = document.getElementById("dictateButton");
  const questionInput = document.getElementById("questionInput");
  const dictateLangList = document.getElementById("dictateLang");
  const SpeechRecognition =
    window.SpeechRecognition || window.webkitSpeechRecognition;

  const languages = {
    "hu-HU": "Magyar",
    "en-US": "English (US)",
    "de-DE": "Deutsch",
    "fr-FR": "Français",
  };

  for (const [langCode, langName] of Object.entries(languages)) {
    const li = document.createElement("li");
    li.className = "mdc-list-item";
    li.setAttribute("data-value", langCode);
    li.innerHTML = `<span class="mdc-list-item__ripple"></span><span class="mdc-list-item__text">${langName}</span>`;
    dictateLangList.appendChild(li);
  }
  dictateSelect.layoutOptions();
  dictateSelect.setValue("hu-HU");

  if (SpeechRecognition) {
    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    dictateButton.addEventListener("click", () => {
      window.stopSpeech();
      recognition.lang = dictateSelect.value;
      recognition.start();
      dictateButton.querySelector(".mdc-button__label").textContent =
        "Figyelek...";
      dictateButton.disabled = true;
    });

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      questionInput.value += (questionInput.value ? " " : "") + transcript;
      // A label frissítése, ha a textfield üres volt
      const textField = textFields.find((tf) =>
        tf.root.contains(questionInput),
      );
      if (textField) textField.layout();
    };

    recognition.onspeechend = () => {
      recognition.stop();
      dictateButton.querySelector(".mdc-button__label").textContent =
        "Diktálás";
      dictateButton.disabled = false;
      questionInput.focus();
      const len = questionInput.value.length;
      questionInput.setSelectionRange(len, len);
    };

    recognition.onerror = (event) => {
      console.error("Beszédfelismerési hiba:", event.error);
      dictateButton.querySelector(".mdc-button__label").textContent =
        "Diktálás";
      dictateButton.disabled = false;
    };
  } else {
    dictateButton.style.display = "none";
    dictateSelect.root.style.display = "none";
  }

  // Keresés a legördülő menükben
  const setupSelectSearch = (searchInputId, listId, mdcSelectInstance) => {
    const searchInput = document.getElementById(searchInputId);
    if (!searchInput) return;

    // Megakadályozza a menü bezáródását a keresőmezőre kattintva
    searchInput.parentElement.addEventListener("click", (e) =>
      e.stopPropagation(),
    );

    searchInput.addEventListener("input", (e) => {
      const searchText = e.target.value.toLowerCase();
      const listItems = document.querySelectorAll(
        `#${listId} > .mdc-list-item`,
      );
      listItems.forEach((item) => {
        const text = item
          .querySelector(".mdc-list-item__text")
          ?.textContent.toLowerCase();
        if (text && text.includes(searchText)) {
          item.style.display = "";
        } else {
          item.style.display = "none";
        }
      });
    });

    // Törli a keresőmezőt és visszaállítja a listát, amikor a menü bezárul
    mdcSelectInstance.menu.listen("MDCMenu:closed", () => {
      setTimeout(() => {
        searchInput.value = "";
        const listItems = document.querySelectorAll(
          `#${listId} > .mdc-list-item`,
        );
        listItems.forEach((item) => {
          item.style.display = "";
        });
      }, 100);
    });
  };

  setupSelectSearch("dictate-search", "dictateLang", dictateSelect);
  setupSelectSearch("voice-search", "voiceSelect", voiceSelectMdc);
});
