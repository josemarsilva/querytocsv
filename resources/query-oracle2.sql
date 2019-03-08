 SELECT ''                                 AS "RAZÃO SOCIAL", 
        ''                                 AS "CNPJ", 
        ''                                 AS "CNPJ_2", 
        ''                                 AS "Tipo de Documento", 
        ''                                 AS "DATA DE RECEBIMENTO", 
        ''                                 AS "FILIAL", 
        TO_CHAR(m.nu_customer)             AS "Maquineta", 
        ''                                 AS "Diretoria", 
        ''                                 AS "Gerência", 
        ''                                 AS "Célula", 
        ''                                 AS "Gestor", 
        ''                                 AS "Numero Cadeia Forçada", 
        ''                                 AS "Nome Cadeia Forçada"
 FROM   TBCTMR_MERCHANT m
 WHERE  1=1
        /* Condition */ 
 AND    EXISTS
        (
          SELECT 'X' FROM PERFSTAR.PERF_CLIENTE_TRANSACAO ct WHERE ct.nu_customer = m.nu_customer
        )
 AND    ROWNUM <= 100
 ORDER  BY m.nu_customer