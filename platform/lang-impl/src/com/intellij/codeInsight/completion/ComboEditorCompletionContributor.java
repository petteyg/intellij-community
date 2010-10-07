/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.codeInsight.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.StringComboboxEditor;

import javax.swing.*;

/**
 * @author peter
 */
public class ComboEditorCompletionContributor extends CompletionContributor{

  @Override
  public void fillCompletionVariants(final CompletionParameters parameters, final CompletionResultSet result) {
    final CompletionProcess process = CompletionService.getCompletionService().getCurrentCompletion();
    if (process != null && process.isAutopopupCompletion()) {
      return;
    }

    final PsiFile file = parameters.getOriginalFile();
    final Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
    if (document != null) {
      final JComboBox comboBox = document.getUserData(StringComboboxEditor.COMBO_BOX_KEY);
      if (comboBox != null) {
        final CompletionResultSet resultSet = result.withPrefixMatcher(document.getText().substring(0, parameters.getOffset()));
        final int count = comboBox.getItemCount();
        for (int i = 0; i < count; i++) {
          final Object o = comboBox.getItemAt(i);
          if (o instanceof String) {
            resultSet.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create((String)o).setInsertHandler(new InsertHandler<LookupElement>() {
              public void handleInsert(final InsertionContext context, final LookupElement item) {
                final Document document = context.getEditor().getDocument();
                document.deleteString(context.getEditor().getCaretModel().getOffset(), document.getTextLength());
              }
            }), count-i));
          }
        }
        result.stopHere();
      }
    }
  }
}
